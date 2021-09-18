package com.lookballs.http.core.request;

import com.lookballs.http.core.BodyType;
import com.lookballs.http.core.body.JsonBody;
import com.lookballs.http.core.body.TextBody;
import com.lookballs.http.core.listener.OnHttpListener;
import com.lookballs.http.core.listener.OnUploadListener;
import com.lookballs.http.core.utils.QuickUtils;
import com.lookballs.http.internal.body.ProgressBody;
import com.lookballs.http.internal.body.UploadBody;
import com.lookballs.http.internal.define.HttpHeaders;
import com.lookballs.http.internal.define.HttpParams;
import com.lookballs.http.internal.define.HttpUrlParams;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 带RequestBody的请求
 */
public abstract class BaseBodyRequest<T extends BaseBodyRequest> extends BaseRequest<T> {

    private RequestBody mRequestBody;//RequestBody
    private long mRefreshTime = 10;//上传回调进度刷新时间，默认10毫秒
    private OnUploadListener mOnUploadListener;//上传回调监听

    //设置请求参数
    public T addParam(Map<String, Object> params) {
        if (mParams == null) {
            mParams = new HttpParams();
        }
        mParams.putAll(params);
        return (T) this;
    }

    //设置请求参数
    public T addParam(String key, Object value) {
        if (mParams == null) {
            mParams = new HttpParams();
        }
        mParams.put(key, value);
        return (T) this;
    }

    //设置RequestBody
    public T addBody(RequestBody body) {
        mRequestBody = body;
        return (T) this;
    }

    //设置JsonBody
    public T addJsonBody(JsonBody body) {
        return addBody(body);
    }

    //设置TextBody
    public T addTextBody(TextBody body) {
        return addBody(body);
    }

    //设置BodyType
    public T bodyType(BodyType bodyType) {
        mBodyType = bodyType;
        return (T) this;
    }

    //设置上传回调进度刷新时间
    public T refreshTime(long refreshTime) {
        mRefreshTime = refreshTime;
        return (T) this;
    }

    @Override
    public void async(OnHttpListener listener) {
        if (listener instanceof OnUploadListener) {
            mOnUploadListener = (OnUploadListener) listener;
        }
        super.async(listener);
    }

    @Override
    public <B> void async(Class<B> clazz, OnHttpListener listener) {
        if (listener instanceof OnUploadListener) {
            mOnUploadListener = (OnUploadListener) listener;
        }
        super.async(clazz, listener);
    }

    public BaseBodyRequest(String url) {
        super(url);
    }

    @Override
    protected Request createRequest(String url, Object tag, HttpHeaders headers, HttpUrlParams urlParams, HttpParams params, BodyType bodyType) {
        String requestUrl = QuickUtils.getSplitUrl(url, urlParams);
        Request.Builder requestBuilder = QuickUtils.createRequestBuilder(requestUrl, tag, headers);

        RequestBody body = mRequestBody != null ? mRequestBody : createRequestBody(params, bodyType);
        requestBuilder.method(getRequestMethod(), body);

        printParam(requestUrl, tag, getRequestMethod(), headers, urlParams, params);
        return requestBuilder.build();
    }

    //创建RequestBody
    private RequestBody createRequestBody(HttpParams params, BodyType bodyType) {
        RequestBody body;
        if (params.isMultipart() && !params.isEmpty()) {//文件形式
            MultipartBody.Builder partBuilder = new MultipartBody.Builder();
            partBuilder.setType(MultipartBody.FORM);
            for (String key : params.getKeys()) {
                Object object = params.getValue(key);
                //如果这是一个文件
                if (object instanceof File) {
                    MultipartBody.Part part = QuickUtils.createPart(key, (File) object);
                    if (part != null) {
                        partBuilder.addPart(part);
                    }
                    continue;
                }
                //如果这是一个输入流
                if (object instanceof InputStream) {
                    MultipartBody.Part part = QuickUtils.createPart(key, (InputStream) object);
                    if (part != null) {
                        partBuilder.addPart(part);
                    }
                    continue;
                }
                //如果这是一个自定义的MultipartBody.Part对象
                if (object instanceof MultipartBody.Part) {
                    partBuilder.addPart((MultipartBody.Part) object);
                    continue;
                }
                //如果这是一个自定义RequestBody
                if (object instanceof RequestBody) {
                    if (object instanceof UploadBody) {
                        partBuilder.addFormDataPart(key, QuickUtils.encodeString(((UploadBody) object).getName()), (RequestBody) object);
                    } else {
                        partBuilder.addFormDataPart(key, null, (RequestBody) object);
                    }
                    continue;
                }
                //如果这是一个文件列表
                if (object instanceof List && QuickUtils.isFileList((List) object)) {
                    for (Object item : (List) object) {
                        MultipartBody.Part part = QuickUtils.createPart(key, (File) item);
                        if (part != null) {
                            partBuilder.addPart(part);
                        }
                    }
                    continue;
                }
                //如果这是一个普通参数
                partBuilder.addFormDataPart(key, String.valueOf(object));
            }
            try {
                body = partBuilder.build();
            } catch (Exception e) {
                //如果partBuilder.build()后参数为空则会抛出异常：Multipart body must have at least one part.
                body = new FormBody.Builder().build();
            }
        } else if (bodyType == BodyType.JSON) {//json形式
            body = new JsonBody(params.getParams());
        } else {//默认表单形式
            FormBody.Builder formBuilder = new FormBody.Builder();
            if (!params.isEmpty()) {
                for (String key : params.getKeys()) {
                    formBuilder.add(key, String.valueOf(params.getValue(key)));
                }
            }
            body = formBuilder.build();
        }
        if (mOnUploadListener != null) {
            return new ProgressBody(body, getLifecycleOwner(), isBindLife(), mRefreshTime, mOnUploadListener);
        } else {
            return body;
        }
    }

}