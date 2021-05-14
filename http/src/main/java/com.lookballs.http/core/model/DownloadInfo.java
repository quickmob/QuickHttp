package com.lookballs.http.core.model;

import com.lookballs.http.utils.QuickUtils;

/**
 * 下载信息
 */
public final class DownloadInfo {

    private final String mFilePath;//文件对象路径

    private long mTotalLength;//总字节数
    private long mDownloadLength;//已下载字节数
    private long mRefreshTime;//下载回调进度刷新时间
    private long mBreakpointLength;//断点续传下载起始位置
    private boolean mIsBreakpoint;//是否开启断点续传下载
    private boolean mIsFinish;//下载完成

    public DownloadInfo(String filePath) {
        mFilePath = filePath;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public long getTotalLength() {
        //如果没有获取到下载内容的大小，就直接返回已下载字节大小
        if (mTotalLength <= 0) {
            return mDownloadLength;
        }
        return mTotalLength;
    }

    public void setTotalLength(long length) {
        mTotalLength = length;
    }

    public long getDownloadLength() {
        return mDownloadLength;
    }

    public void setDownloadLength(long length) {
        mDownloadLength = length;
    }

    public long getRefreshTime() {
        return mRefreshTime;
    }

    public void setRefreshTime(long refreshTime) {
        this.mRefreshTime = refreshTime;
    }

    public long getBreakpointLength() {
        return mBreakpointLength;
    }

    public void setBreakpointLength(long breakpointLength) {
        this.mBreakpointLength = breakpointLength;
    }

    public boolean isBreakpoint() {
        return mIsBreakpoint;
    }

    public void setBreakpoint(boolean isBreakpoint) {
        this.mIsBreakpoint = isBreakpoint;
    }

    public boolean isFinish() {
        return mIsFinish;
    }

    public void setFinish(boolean isFinish) {
        this.mIsFinish = isFinish;
    }

    public int getProgress() {
        int progress = QuickUtils.getProgress(getTotalLength(), getDownloadLength());
        if (progress < 0) {
            return 0;
        } else if (progress > 100) {
            return 100;
        }
        return progress;
    }

    @Override
    public String toString() {
        return "DownloadInfo{" +
                "mFilePath=" + mFilePath +
                ", progress=" + getProgress() +
                ", mTotalLength=" + mTotalLength +
                ", mDownloadLength=" + mDownloadLength +
                ", mRefreshTime=" + mRefreshTime +
                ", mBreakpointLength=" + mBreakpointLength +
                ", mIsBreakpoint=" + mIsBreakpoint +
                ", mIsFinish=" + mIsFinish +
                '}';
    }

}