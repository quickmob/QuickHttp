package com.lookballs.app.http.http.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 自定义公共请求头参数拦截器
 */
public class CustomHeadersInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        Request.Builder requestBuilder = request.newBuilder();
        //设置User-Agent
        requestBuilder.removeHeader("User-Agent");
        requestBuilder.header("User-Agent", "");

        Response response = chain.proceed(requestBuilder.build());
        return response;
    }
}
