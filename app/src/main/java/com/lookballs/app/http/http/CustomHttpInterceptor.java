package com.lookballs.app.http.http;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络请求拦截器
 */
public class CustomHttpInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        Request.Builder requestBuilder = request.newBuilder();
        //设置User-Agent
        requestBuilder.removeHeader("User-Agent");
        //requestBuilder.addHeader("User-Agent", Util.getUserAgent());
        requestBuilder.url(getRequestUrl(request));

        Response response = chain.proceed(requestBuilder.build());
        return response;
    }

    private String getRequestUrl(Request request) {
        return request.url().toString();
    }
}
