package com.lookballs.http;

import androidx.lifecycle.LifecycleOwner;

import com.lookballs.http.core.request.DeleteRequest;
import com.lookballs.http.core.request.DownloadRequest;
import com.lookballs.http.core.request.GetRequest;
import com.lookballs.http.core.request.HeadRequest;
import com.lookballs.http.core.request.PatchRequest;
import com.lookballs.http.core.request.PostRequest;
import com.lookballs.http.core.request.PutRequest;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * 网络请求类
 */
public class QuickHttp {

    public static final int ERROR_CODE = -2020;

    private static HttpConfig mConfig;

    private QuickHttp() {

    }

    /**
     * 获取配置
     *
     * @return
     */
    public static HttpConfig getConfig() {
        return mConfig;
    }

    /**
     * 初始化配置
     *
     * @param config
     * @return
     */
    public static synchronized void init(HttpConfig config) {
        if (mConfig == null) {
            checkConfig(config);
            mConfig = config;
        }
    }

    /**
     * 判断是否已经初始化
     *
     * @return
     */
    public static boolean isInit() {
        return mConfig != null;
    }

    /**
     * 检查配置
     *
     * @param config
     */
    private static void checkConfig(HttpConfig config) {
        if (config == null) {
            throw new NullPointerException(">>>HttpConfig object == null");
        } else {
            if (config.getContext() == null) {
                throw new NullPointerException(">>>HttpConfig.context object == null");
            }
            if (config.getDataConverter() == null) {
                throw new NullPointerException(">>>HttpConfig.dataConverter object == null");
            }
        }
    }

    /**
     * get请求
     *
     * @param url
     * @return
     */
    public static GetRequest get(String url) {
        return GetRequest.with(url);
    }

    /**
     * head请求
     *
     * @param url
     * @return
     */
    public static HeadRequest head(String url) {
        return HeadRequest.with(url);
    }

    /**
     * post请求
     *
     * @param url
     * @return
     */
    public static PostRequest post(String url) {
        return PostRequest.with(url);
    }

    /**
     * delete请求
     *
     * @param url
     * @return
     */
    public static DeleteRequest delete(String url) {
        return DeleteRequest.with(url);
    }

    /**
     * patch请求
     *
     * @param url
     * @return
     */
    public static PatchRequest patch(String url) {
        return PatchRequest.with(url);
    }

    /**
     * put请求
     *
     * @param url
     * @return
     */
    public static PutRequest put(String url) {
        return PutRequest.with(url);
    }

    /**
     * download请求
     *
     * @param url
     * @return
     */
    public static DownloadRequest download(String url) {
        return DownloadRequest.with(url);
    }

    /**
     * 取消请求任务
     */
    public static void cancel(LifecycleOwner lifecycleOwner) {
        cancelTag(lifecycleOwner);
    }

    /**
     * 根据tag取消请求任务
     *
     * @param tag
     */
    public static void cancelTag(Object tag) {
        cancelTag(getConfig().getOkHttpClient(), tag);
    }

    /**
     * 根据tag取消请求任务
     *
     * @param okHttpClient
     * @param tag
     */
    public static void cancelTag(OkHttpClient okHttpClient, Object tag) {
        if (okHttpClient == null || tag == null) return;
        //清除排队等候的任务
        for (Call call : okHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        //清除正在执行的任务
        for (Call call : okHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    /**
     * 取消所有请求任务
     */
    public static void cancelAll() {
        cancelAll(getConfig().getOkHttpClient());
    }

    /**
     * 取消所有请求任务
     */
    public static void cancelAll(OkHttpClient okHttpClient) {
        if (okHttpClient == null) return;
        //清除排队等候的任务
        for (Call call : okHttpClient.dispatcher().queuedCalls()) {
            call.cancel();
        }
        //清除正在执行的任务
        for (Call call : okHttpClient.dispatcher().runningCalls()) {
            call.cancel();
        }
    }

}