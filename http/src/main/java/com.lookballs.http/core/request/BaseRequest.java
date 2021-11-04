package com.lookballs.http.core.request;

import android.text.TextUtils;

import androidx.lifecycle.LifecycleOwner;

import com.lookballs.http.QuickHttp;
import com.lookballs.http.core.BodyType;
import com.lookballs.http.core.cache.CacheConfig;
import com.lookballs.http.core.cache.CacheMode;
import com.lookballs.http.core.converter.IDataConverter;
import com.lookballs.http.core.interceptor.CacheInterceptor;
import com.lookballs.http.core.listener.OnHttpListener;
import com.lookballs.http.core.listener.OnRetryConditionListener;
import com.lookballs.http.core.utils.QuickLogUtils;
import com.lookballs.http.core.utils.QuickUtils;
import com.lookballs.http.internal.callback.NormalCallback;
import com.lookballs.http.internal.define.HttpCall;
import com.lookballs.http.internal.define.HttpHeaders;
import com.lookballs.http.internal.define.HttpParams;
import com.lookballs.http.internal.define.HttpUrlParams;

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
    protected boolean mBindLife;//是否绑定生命周期
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
    protected long mDelayMillis = 0;//延时请求时间(毫秒)
    protected String mBaseUrl;//基本url
    protected int mRetryCount = 0;//重试次数
    protected long mRetryDelayMillis = 0;//重试延时时间(毫秒)
    protected OnRetryConditionListener mOnRetryConditionListener = null;//重试条件回调
    protected long mConnectTimeout = 0;//客户端连接服务器的超时时长
    protected long mReadTimeout = 0;//单次读取数据的超时时长，如下载文件时，会读取多次
    protected long mWriteTimeout = 0;//单次写数据的超时时长，如上传文件时，会写多次
    protected TimeUnit mConnectTimeOutUnit = TimeUnit.MILLISECONDS;//连接超时的时间单位
    protected TimeUnit mReadTimeoutUnit = TimeUnit.MILLISECONDS;//读取超时的时间单位
    protected TimeUnit mWriteTimeoutUnit = TimeUnit.MILLISECONDS;//写超时的时间单位
    protected CacheConfig mCacheConfig;//缓存配置

    public BaseRequest(String url) {
        mUrl = url;
        mCacheConfig = QuickHttp.getConfig().getCacheConfig();
    }

    /**
     * 绑定生命周期
     *
     * @param lifecycleOwner 有生命周期的对象（例如AppCompatActivity、FragmentActivity、Fragment等）
     *                       如需传入其他对象请参考以下两个类
     *                       {@link com.lookballs.http.core.lifecycle.ActivityLifecycle}
     *                       {@link com.lookballs.http.core.lifecycle.ApplicationLifecycle}
     * @return
     */
    public T bindLife(LifecycleOwner lifecycleOwner) {
        mLifecycleOwner = lifecycleOwner;
        if (mLifecycleOwner != null) {
            mBindLife = true;
            tag(lifecycleOwner);
        }
        return (T) this;
    }

    //设置OkHttpClient
    public T client(OkHttpClient okHttpClient) {
        mOkHttpClient = okHttpClient;
        return (T) this;
    }

    //设置延时请求时间
    public T delayMillis(long delayMillis) {
        mDelayMillis = delayMillis;
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

    //设置请求tag
    public T tag(Object tag) {
        mTag = tag;
        return (T) this;
    }

    //设置请求头
    public T addHeader(Map<String, String> params) {
        if (mHeaders == null) {
            mHeaders = new HttpHeaders();
        }
        mHeaders.putAll(params);
        return (T) this;
    }

    //设置请求头
    public T addHeader(String key, String value) {
        if (mHeaders == null) {
            mHeaders = new HttpHeaders();
        }
        mHeaders.put(key, value);
        return (T) this;
    }

    //设置请求url参数
    public T addUrlParam(Map<String, Object> params) {
        if (mUrlParams == null) {
            mUrlParams = new HttpUrlParams();
        }
        mUrlParams.putAll(params);
        return (T) this;
    }

    //设置请求url参数
    public T addUrlParam(String key, Object value) {
        if (mUrlParams == null) {
            mUrlParams = new HttpUrlParams();
        }
        mUrlParams.put(key, value);
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
    public T assemblyEnabled(boolean assemblyHeaders, boolean assemblyUrlParams) {
        return assemblyEnabled(assemblyHeaders, true, assemblyUrlParams);
    }

    //设置公共包装参数开关
    public T assemblyEnabled(boolean assemblyHeaders, boolean assemblyParams, boolean assemblyUrlParams) {
        mAssemblyHeaders = assemblyHeaders;
        mAssemblyParams = assemblyParams;
        mAssemblyUrlParams = assemblyUrlParams;
        return (T) this;
    }

    //设置基本url
    public T baseUrl(String baseUrl) {
        mBaseUrl = baseUrl;
        return (T) this;
    }

    //设置缓存key
    public T cacheKey(String cacheKey) {
        mCacheConfig.setCacheKey(cacheKey);
        return (T) this;
    }

    //设置缓存模式
    public T cacheMode(CacheMode cacheMode) {
        mCacheConfig.setCacheMode(cacheMode);
        return (T) this;
    }

    //设置缓存缓存有效时长
    public T cacheValidTime(long cacheValidTime) {
        mCacheConfig.setCacheValidTime(cacheValidTime);
        return (T) this;
    }

    //开始同步请求
    public <B> B sync(Class<B> clazz) throws Exception {
        if (mDelayMillis > 0) {
            Thread.sleep(mDelayMillis);
        }
        return syncRequest(clazz);
    }

    //开始异步请求
    public void async(OnHttpListener listener) {
        if (mDelayMillis > 0) {
            QuickUtils.postDelayed(new Runnable() {
                @Override
                public void run() {
                    asyncRequest(null, listener);
                }
            }, mDelayMillis);
        } else {
            asyncRequest(null, listener);
        }
    }

    //开始异步请求
    public <B> void async(Class<B> clazz, OnHttpListener listener) {
        if (mDelayMillis > 0) {
            QuickUtils.postDelayed(new Runnable() {
                @Override
                public void run() {
                    asyncRequest(clazz, listener);
                }
            }, mDelayMillis);
        } else {
            asyncRequest(clazz, listener);
        }
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

        String baseUrl;
        if (QuickUtils.checkHttpUrl(mUrl)) {
            baseUrl = mUrl;
        } else {
            if (!TextUtils.isEmpty(mBaseUrl)) {
                baseUrl = mBaseUrl + mUrl;
            } else {
                if (!TextUtils.isEmpty(QuickHttp.getConfig().getBaseUrl())) {
                    baseUrl = QuickHttp.getConfig().getBaseUrl() + mUrl;
                } else {
                    baseUrl = mUrl;
                }
            }
        }
        String url = QuickUtils.getSplitUrl(baseUrl, mUrlParams.getParams());
        if (TextUtils.isEmpty(mCacheConfig.getCacheKey())) {
            String key = QuickUtils.buildCacheKey(url, mUrlParams.getParams(), mParams.getParams());
            mCacheConfig.setCacheKey(key);
        }
        return createOkHttpClient().newCall(createRequest(url, mTag, mHeaders, mUrlParams, mParams, mBodyType));
    }

    //获取LifecycleOwner
    protected LifecycleOwner getLifecycleOwner() {
        return mLifecycleOwner;
    }

    //是否绑定生命周期
    protected boolean isBindLife() {
        return mBindLife;
    }

    //打印参数
    protected void printParam(String url, Object tag, String method, HttpHeaders headers, HttpUrlParams urlParams, HttpParams params) {
        StringBuilder sb = new StringBuilder();
        sb.append("请求参数").append("\n");
        sb.append("Url：").append(url).append("\n");
        sb.append("Tag：").append(tag).append("\n");
        sb.append("Method：").append(method).append("\n");
        sb.append("HttpHeaders：").append(headers.isEmpty() ? "" : new JSONObject(headers.getHeaders())).append("\n");
        sb.append("HttpParams：").append(params.isEmpty() ? "" : new JSONObject(params.getParams())).append("\n");
        sb.append("HttpUrlParams：").append(urlParams.isEmpty() ? "" : new JSONObject(urlParams.getParams())).append("\n");
        QuickLogUtils.json(sb.toString());
    }

    //获取当前请求方式
    protected abstract String getRequestMethod();

    //创建Request
    protected abstract Request createRequest(String url, Object tag, HttpHeaders headers, HttpUrlParams urlParams, HttpParams params, BodyType bodyType);

    //创建同步请求
    private <B> B syncRequest(Class<B> clazz) throws Exception {
        try {
            mHttpCall = new HttpCall(createCall());
            Response response = mHttpCall.execute();
            if (mDataConverter != null) {
                return (B) mDataConverter.onSucceed(getLifecycleOwner(), mUrl, response, QuickUtils.getParameterizedType(clazz));
            }
            return (B) QuickHttp.getConfig().getDataConverter().onSucceed(getLifecycleOwner(), mUrl, response, QuickUtils.getParameterizedType(clazz));
        } catch (Exception e) {
            if (mDataConverter != null) {
                throw mDataConverter.onFail(getLifecycleOwner(), mUrl, e);
            }
            throw QuickHttp.getConfig().getDataConverter().onFail(getLifecycleOwner(), mUrl, e);
        }
    }

    //创建异步请求
    private void asyncRequest(Class clazz, OnHttpListener listener) {
        if (clazz != null) {
            mHttpCall = new HttpCall(createCall());
            mHttpCall.enqueue(new NormalCallback(getLifecycleOwner(), isBindLife(), mHttpCall, mRetryCount, mRetryDelayMillis, mUrl, mOnRetryConditionListener, listener, clazz, mDataConverter));
        } else {
            mHttpCall = new HttpCall(createCall());
            mHttpCall.enqueue(new NormalCallback(getLifecycleOwner(), isBindLife(), mHttpCall, mRetryCount, mRetryDelayMillis, mUrl, mOnRetryConditionListener, listener, mDataConverter));
        }
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
        if (mCacheConfig.getCacheMode() != CacheMode.ONLY_REQUEST_NETWORK) {
            if (builder == null) {
                builder = okHttpClient.newBuilder();
            }
            builder.addInterceptor(new CacheInterceptor(mCacheConfig, QuickHttp.getConfig().getCacheStrategy()));
        }
        return builder != null ? builder.build() : okHttpClient;
    }
}