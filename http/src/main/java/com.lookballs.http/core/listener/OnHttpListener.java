package com.lookballs.http.core.listener;

import okhttp3.Call;

/**
 * 请求回调监听器
 */
public interface OnHttpListener<T> {

    /**
     * 请求开始
     */
    default void onStart(Call call) {
    }

    /**
     * 请求成功
     */
    void onSucceed(T result);

    /**
     * 请求出错
     */
    void onError(int code, Exception e);

    /**
     * 请求结束
     */
    default void onEnd(Call call) {
    }
}