package com.lookballs.http.core.listener;

import com.lookballs.http.core.model.UploadInfo;

/**
 * 带上传进度回调的监听器
 */
public interface OnUploadListener<T> extends OnHttpListener<T> {

    /**
     * 上传进度改变
     */
    void onProgress(UploadInfo info);
}