package com.lookballs.http.core.request;

import com.lookballs.http.core.model.BodyType;
import com.lookballs.http.core.model.HttpHeaders;
import com.lookballs.http.core.model.HttpParams;
import com.lookballs.http.core.model.HttpUrlParams;
import com.lookballs.http.utils.QuickUtils;

import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * 不带RequestBody的请求
 */
public abstract class UrlRequest<T extends UrlRequest> extends BaseRequest<T> {

    private CacheControl mCacheControl;//缓存控制器

    //设置缓存控制器
    public T cache(CacheControl cacheControl) {
        mCacheControl = cacheControl;
        return (T) this;
    }

    public UrlRequest(String url) {
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

        printParam(requestUrl, tag, getRequestMethod(), mRetryCount, headers, urlParams, params);
        return requestBuilder.build();
    }

}