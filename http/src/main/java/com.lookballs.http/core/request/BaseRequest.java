package com.lookballs.http.core.request;

import androidx.lifecycle.LifecycleOwner;

import com.lookballs.http.QuickHttp;
import com.lookballs.http.core.converter.IDataConverter;
import com.lookballs.http.core.model.BodyType;
import com.lookballs.http.core.model.HttpCall;
import com.lookballs.http.core.model.HttpHeaders;
import com.lookballs.http.core.model.HttpParams;
import com.lookballs.http.core.model.HttpUrlParams;
import com.lookballs.http.internal.callback.NormalCallback;
import com.lookballs.http.listener.OnHttpListener;
import com.lookballs.http.listener.OnRetryConditionListener;
import com.lookballs.http.utils.QuickLogUtils;
import com.lookballs.http.utils.QuickUtils;

import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 请求基类
 */
public abstract class BaseRequest<T extends BaseRequest> {

    protected LifecycleOwner mLifecycleOwner;//生命周期控制对象
    protected Object mTag;//请求tag
    protected String mUrl;//请求url
    protected HttpHeaders mHeaders;//请求头
    protected HttpParams mParams;//请求参数
    protected HttpUrlParams mUrlParams;//请求url参数
    protected BodyType mBodyType;//BodyType
    protected OkHttpClient mOkHttpClient;//OkHttpClient对象
    protected HttpCall mHttpCall;//请求对象代理
    protected IDataConverter mDataConverter;//数据转换器
    protected boolean mAssemblyHeaders = true;//是否使用公共请求头参数开关
    protected boolean mAssemblyParams = true;//是否使用公共请求参数开关
    protected boolean mAssemblyUrlParams = true;//是否使用公共请求url参数开关

    protected int mRetryCount = 0;//重试次数
    protected long mRetryDelayMillis = 0;//重试延时时间(毫秒)
    protected OnRetryConditionListener mOnRetryConditionListener = null;//重试条件回调
    protected long mConnectTimeout = 0;//客户端连接服务器的超时时长
    protected long mReadTimeout = 0;//单次读取数据的超时时长，如下载文件时，会读取多次
    protected long mWriteTimeout = 0;//单次写数据的超时时长，如上传文件时，会写多次
    protected TimeUnit mConnectTimeOutUnit = TimeUnit.MILLISECONDS;//连接超时的时间单位
    protected TimeUnit mReadTimeoutUnit = TimeUnit.MILLISECONDS;//读取超时的时间单位
    protected TimeUnit mWriteTimeoutUnit = TimeUnit.MILLISECONDS;//写超时的时间单位

    public BaseRequest(LifecycleOwner lifecycleOwner) {
        mLifecycleOwner = lifecycleOwner;
        tag(lifecycleOwner);
    }

    //设置OkHttpClient
    public T client(OkHttpClient okHttpClient) {
        mOkHttpClient = okHttpClient;
        return (T) this;
    }

    //设置请求重试
    public T retry(int retryCount) {
        return retry(retryCount, 0, null);
    }

    //设置请求重试
    public T retry(int retryCount, long retryDelayMillis) {
        return retry(retryCount, retryDelayMillis, null);
    }

    //设置请求重试
    public T retry(int retryCount, OnRetryConditionListener listener) {
        return retry(retryCount, 0, listener);
    }

    //设置请求重试
    public T retry(int retryCount, long retryDelayMillis, OnRetryConditionListener listener) {
        if (retryCount < 0) {
            retryCount = 0;
        }
        if (retryDelayMillis < 0) {
            retryDelayMillis = 0;
        }
        mRetryCount = retryCount;
        mRetryDelayMillis = retryDelayMillis;
        mOnRetryConditionListener = listener;
        return (T) this;
    }

    //设置连接超时时长
    public T connectTimeout(long connectTimeout, TimeUnit unit) {
        if (connectTimeout < 0) {
            connectTimeout = 0;
        }
        mConnectTimeout = connectTimeout;
        mConnectTimeOutUnit = unit;
        return (T) this;
    }

    //设置读取超时时长
    public T readTimeout(long readTimeout, TimeUnit unit) {
        if (readTimeout < 0) {
            readTimeout = 0;
        }
        mReadTimeout = readTimeout;
        mReadTimeoutUnit = unit;
        return (T) this;
    }

    //设置写超时时长
    public T writeTimeout(long writeTimeout, TimeUnit unit) {
        if (writeTimeout < 0) {
            writeTimeout = 0;
        }
        mWriteTimeout = writeTimeout;
        mWriteTimeoutUnit = unit;
        return (T) this;
    }

    //设置请求url
    public T url(String url) {
        mUrl = url;
        return (T) this;
    }

    //设置请求tag
    public T tag(Object tag) {
        mTag = tag;
        return (T) this;
    }

    //设置请求头
    public T headers(HttpHeaders headers) {
        mHeaders = headers;
        return (T) this;
    }

    //设置请求参数
    public T params(HttpParams params) {
        mParams = params;
        return (T) this;
    }

    //设置请求url参数
    public T urlParams(HttpUrlParams urlParams) {
        mUrlParams = urlParams;
        return (T) this;
    }

    //设置BodyType
    public T bodyType(BodyType bodyType) {
        mBodyType = bodyType;
        return (T) this;
    }

    //设置数据转换器
    public T dataConverter(IDataConverter dataConverter) {
        mDataConverter = dataConverter;
        return (T) this;
    }

    //设置公共包装参数开关
    public T assemblyEnabled(boolean assemblyHeaders) {
        return assemblyEnabled(assemblyHeaders, true, true);
    }

    //设置公共包装参数开关
    public T assemblyEnabled(boolean assemblyHeaders, boolean assemblyParams) {
        return assemblyEnabled(assemblyHeaders, assemblyParams, true);
    }

    //设置公共包装参数开关
    public T assemblyEnabled(boolean assemblyHeaders, boolean assemblyParams, boolean assemblyUrlParams) {
        mAssemblyHeaders = assemblyHeaders;
        mAssemblyParams = assemblyParams;
        mAssemblyUrlParams = assemblyUrlParams;
        return (T) this;
    }

    //开始同步请求
    public <B> B sync(Class<B> clazz) throws Exception {
        try {
            mHttpCall = new HttpCall(createCall());
            Response response = mHttpCall.execute();
            if (mDataConverter != null) {
                return (B) mDataConverter.onSucceed(getLifecycleOwner(), response, QuickUtils.getParameterizedType(clazz));
            }
            return (B) QuickHttp.getConfig().getDataConverter().onSucceed(getLifecycleOwner(), response, QuickUtils.getParameterizedType(clazz));
        } catch (Exception e) {
            if (mDataConverter != null) {
                throw mDataConverter.onFail(getLifecycleOwner(), e);
            }
            throw QuickHttp.getConfig().getDataConverter().onFail(getLifecycleOwner(), e);
        }
    }

    //开始异步请求
    public void async(OnHttpListener listener) {
        mHttpCall = new HttpCall(createCall());
        mHttpCall.enqueue(new NormalCallback(getLifecycleOwner(), mHttpCall, mRetryCount, mRetryDelayMillis, mOnRetryConditionListener, listener, mDataConverter));
    }

    //开始异步请求
    public <B> void async(Class<B> clazz, OnHttpListener listener) {
        mHttpCall = new HttpCall(createCall());
        mHttpCall.enqueue(new NormalCallback(getLifecycleOwner(), mHttpCall, mRetryCount, mRetryDelayMillis, mOnRetryConditionListener, listener, clazz, mDataConverter));
    }

    //创建OkHttpClient
    private OkHttpClient createOkHttpClient() {
        if (mOkHttpClient == null) {
            mOkHttpClient = QuickHttp.getConfig().getOkHttpClient();
        }
        final OkHttpClient okHttpClient = mOkHttpClient;
        OkHttpClient.Builder builder = null;
        if (mConnectTimeout != 0) {
            if (builder == null) {
                builder = okHttpClient.newBuilder();
            }
            builder.connectTimeout(mConnectTimeout, mConnectTimeOutUnit);
        }
        if (mReadTimeout != 0) {
            if (builder == null) {
                builder = okHttpClient.newBuilder();
            }
            builder.readTimeout(mReadTimeout, mReadTimeoutUnit);
        }
        if (mWriteTimeout != 0) {
            if (builder == null) {
                builder = okHttpClient.newBuilder();
            }
            builder.writeTimeout(mWriteTimeout, mWriteTimeoutUnit);
        }
        return builder != null ? builder.build() : okHttpClient;
    }

    //创建Call
    protected Call createCall() {
        if (mHeaders == null) {
            mHeaders = new HttpHeaders();
        }
        if (mAssemblyHeaders) {
            if (QuickHttp.getConfig().getAddHeadersListener() != null && QuickHttp.getConfig().getAddHeadersListener().applyMap() != null) {
                Map<String, String> applyMap = QuickHttp.getConfig().getAddHeadersListener().applyMap();
                mHeaders.putAll(applyMap);
            }
        }

        if (mParams == null) {
            mParams = new HttpParams();
        }
        if (mAssemblyParams) {
            if (QuickHttp.getConfig().getAddParamsListener() != null && QuickHttp.getConfig().getAddParamsListener().applyMap() != null) {
                Map<String, Object> applyMap = QuickHttp.getConfig().getAddParamsListener().applyMap();
                mParams.putAll(applyMap);
            }
        }

        if (mUrlParams == null) {
            mUrlParams = new HttpUrlParams();
        }
        if (mAssemblyUrlParams) {
            if (QuickHttp.getConfig().getAddUrlParamsListener() != null && QuickHttp.getConfig().getAddUrlParamsListener().applyMap() != null) {
                Map<String, Object> applyMap = QuickHttp.getConfig().getAddUrlParamsListener().applyMap();
                mUrlParams.putAll(applyMap);
            }
        }

        if (mBodyType == null) {
            mBodyType = QuickHttp.getConfig().getBodyType();
        }
        return createOkHttpClient().newCall(createRequest(mUrl, mTag, mHeaders, mUrlParams, mParams, mBodyType));
    }

    //获取LifecycleOwner
    protected LifecycleOwner getLifecycleOwner() {
        return mLifecycleOwner;
    }

    //打印参数
    protected void printParam(String url, Object tag, String method, int retryCount, HttpHeaders headers, HttpUrlParams urlParams, HttpParams params) {
        StringBuilder sb = new StringBuilder();
        sb.append("请求参数").append("\n");
        sb.append("Url：").append(url).append("\n");
        sb.append("Tag：").append(tag).append("\n");
        sb.append("Method：").append(method).append("\n");
        sb.append("RetryCount：").append(retryCount).append("\n");
        sb.append("HttpHeaders：").append(headers.isEmpty() ? "" : new JSONObject(headers.getHeaders())).append("\n");
        sb.append("HttpParams：").append(params.isEmpty() ? "" : new JSONObject(params.getParams())).append("\n");
        sb.append("HttpUrlParams：").append(urlParams.isEmpty() ? "" : new JSONObject(urlParams.getParams())).append("\n");
        QuickLogUtils.json(sb.toString());
    }

    //获取当前请求方式
    protected abstract String getRequestMethod();

    //创建Request
    protected abstract Request createRequest(String url, Object tag, HttpHeaders headers, HttpUrlParams urlParams, HttpParams params, BodyType bodyType);

}