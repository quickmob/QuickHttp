package com.lookballs.http.core.listener;

import com.lookballs.http.core.model.DownloadInfo;

import okhttp3.Call;

/**
 * 下载监听器
 */
public interface OnDownloadListener {

    /**
     * 下载进度改变
     */
    default void onProgress(DownloadInfo info) {
    }

    /**
     * 下载速度
     */
    default void onSpeed(DownloadInfo info, long downloadSecond, long downloadSize) {
    }

    /**
     * 下载完成
     */
    void onComplete(DownloadInfo info);

    /**
     * 下载出错
     */
    void onError(DownloadInfo info, Exception e);

    /**
     * 下载开始
     */
    default void onStart(Call call) {
    }

    /**
     * 下载结束
     */
    default void onEnd(Call call) {
    }
}