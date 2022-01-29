package com.lookballs.app.http.http;

import com.lookballs.app.http.http.interceptor.CustomHeadersInterceptor;
import com.lookballs.http.core.utils.HttpsUtils;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import okhttp3.Dns;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * 自定义OkHttpClient
 */
public class CustomOkHttpClient {

    private OkHttpClient okHttpClient;

    private static CustomOkHttpClient instance;

    public static CustomOkHttpClient getInstance() {
        if (instance == null) {
            instance = new CustomOkHttpClient();
        }
        return instance;
    }

    public void createOkHttpClient(long timeout, Dns dns) {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                    .readTimeout(timeout, TimeUnit.MILLISECONDS)
                    .writeTimeout(timeout, TimeUnit.MILLISECONDS)
                    .sslSocketFactory(HttpsUtils.getSslSocketFactory().sSLSocketFactory, HttpsUtils.getSslSocketFactory().trustManager)
                    .hostnameVerifier(HttpsUtils.UnSafeHostnameVerifier)
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))//TODO 如果是要上传文件和下载文件，这里不能传BODY
                    .addInterceptor(new CustomHeadersInterceptor())
                    .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                    .dns(dns)
                    .build();
        }
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

}
