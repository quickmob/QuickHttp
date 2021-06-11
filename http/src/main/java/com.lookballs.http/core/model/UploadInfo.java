package com.lookballs.http.core.model;

import com.lookballs.http.utils.QuickUtils;

/**
 * 上传信息
 */
public final class UploadInfo {

    private long mTotalLength;//总字节数
    private long mUploadLength;//已上传字节数

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

    public int getProgress() {
        int progress = QuickUtils.getProgress(getTotalLength(), getUploadLength());
        if (progress < 0) {
            return 0;
        } else if (progress > 100) {
            return 100;
        }
        return progress;
    }

    public double getPreciseProgress() {
        double progress = QuickUtils.getPreciseProgress(getTotalLength(), getUploadLength());
        if (progress < 0.00) {
            return 0.00;
        } else if (progress > 100.00) {
            return 100.00;
        }
        return progress;
    }

    public String getTextPreciseProgress() {
        //如果最后一位小数是0时，double值会默认将0去掉，这里将0补齐
        return String.format("%.2f", getPreciseProgress());
    }

    @Override
    public String toString() {
        return "UploadInfo{" +
                "progress=" + getProgress() +
                ", preciseProgress=" + getPreciseProgress() +
                ", mTotalLength=" + mTotalLength +
                ", mUploadLength=" + mUploadLength +
                '}';
    }
}