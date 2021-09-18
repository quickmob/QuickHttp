package com.lookballs.http.core.request;

import com.lookballs.http.core.BodyType;
import com.lookballs.http.core.utils.QuickUtils;
import com.lookballs.http.internal.define.HttpHeaders;
import com.lookballs.http.internal.define.HttpParams;
import com.lookballs.http.internal.define.HttpUrlParams;

import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * 不带RequestBody的请求
 */
public abstract class BaseUrlRequest<T extends BaseUrlRequest> extends BaseRequest<T> {

    private CacheControl mCacheControl;//缓存控制器

    //设置缓存控制器
    public T cache(CacheControl cacheControl) {
        mCacheControl = cacheControl;
        return (T) this;
    }

    public BaseUrlRequest(String url) {
        super(url);
    }

    @Override
    protected Request createRequest(String url, Object tag, HttpHeaders headers, HttpUrlParams urlParams, HttpParams params, BodyType bodyType) {
        String requestUrl = QuickUtils.getSplitUrl(url, urlParams);
        Request.Builder requestBuilder = QuickUtils.createRequestBuilder(requestUrl, tag, headers);
        if (mCacheControl != null) {
            requestBuilder.cacheControl(mCacheControl);
        }

        HttpUrl.Builder builder = HttpUrl.get(url).newBuilder();
        HttpUrl link = builder.build();
        requestBuilder.url(link);
        requestBuilder.method(getRequestMethod(), null);

        printParam(requestUrl, tag, getRequestMethod(), headers, urlParams, params);
        return requestBuilder.build();
    }

}