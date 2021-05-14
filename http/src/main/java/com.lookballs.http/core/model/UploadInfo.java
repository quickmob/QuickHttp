package com.lookballs.http.core.model;

import com.lookballs.http.utils.QuickUtils;

/**
 * 上传信息
 */
public final class UploadInfo {

    private long mTotalLength;//总字节数
    private long mUploadLength;//已上传字节数
    private long mRefreshTime;//上传回调进度刷新时间

    public long getTotalLength() {
        return mTotalLength;
    }

    public void setTotalLength(long totalLength) {
        this.mTotalLength = totalLength;
    }

    public long getUploadLength() {
        return mUploadLength;
    }

    public void setUploadLength(long uploadLength) {
        this.mUploadLength = uploadLength;
    }

    public long getRefreshTime() {
        return mRefreshTime;
    }

    public void setRefreshTime(long refreshTime) {
        this.mRefreshTime = refreshTime;
    }

    public int getProgress() {
        int progress = QuickUtils.getProgress(getTotalLength(), getUploadLength());
        if (progress < 0) {
            return 0;
        } else if (progress > 100) {
            return 100;
        }
        return progress;
    }

    @Override
    public String toString() {
        return "UploadInfo{" +
                "mTotalLength=" + mTotalLength +
                ", progress=" + getProgress() +
                ", mUploadLength=" + mUploadLength +
                ", mRefreshTime=" + mRefreshTime +
                '}';
    }
}