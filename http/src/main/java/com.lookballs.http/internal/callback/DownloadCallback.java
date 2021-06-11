package com.lookballs.http.internal.callback;

import android.os.SystemClock;
import android.text.TextUtils;

import androidx.lifecycle.LifecycleOwner;

import com.lookballs.http.core.exception.MD5Exception;
import com.lookballs.http.core.exception.NullBodyException;
import com.lookballs.http.core.exception.ResponseException;
import com.lookballs.http.core.lifecycle.HttpLifecycleManager;
import com.lookballs.http.core.model.DownloadInfo;
import com.lookballs.http.core.model.HttpCall;
import com.lookballs.http.listener.OnDownloadListener;
import com.lookballs.http.listener.OnRetryConditionListener;
import com.lookballs.http.utils.QuickLogUtils;
import com.lookballs.http.utils.QuickUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.Response;

/**
 * 下载接口回调
 */
public final class DownloadCallback extends BaseCallback {

    private final String TAG = "DownloadCallback>>>";

    private static final String FILE_MD5_REGEX = "^[\\w]{32}$";//文件 MD5 正则表达式
    private final DownloadInfo mDownloadInfo;//下载任务
    private final File mFile;//保存的文件
    private final boolean mIsBreakpoint;//是否开启断点续传下载
    private final long mRefreshTime;//下载回调进度刷新时间
    private final long mBreakpointLength;//断点续传下载起始位置
    private final OnDownloadListener mListener;//下载监听回调

    private String mMD5;//校验的MD5
    private double lastProgress;//最后一次刷新的进度
    private long lastRefreshTime = 0L;//最后一次刷新进度的时间

    public DownloadCallback(LifecycleOwner lifecycleOwner, boolean isBindLife, HttpCall call, int retryCount, long retryDelayMillis, OnRetryConditionListener onRetryConditionListener, String filePath, String md5, long refreshTime, long breakpointLength, boolean isBreakpoint, OnDownloadListener listener) {
        super(lifecycleOwner, isBindLife, call, retryCount, retryDelayMillis, onRetryConditionListener);
        mDownloadInfo = new DownloadInfo(filePath);
        mDownloadInfo.setRefreshTime(refreshTime);
        mDownloadInfo.setBreakpointLength(breakpointLength);
        mDownloadInfo.setBreakpoint(isBreakpoint);
        mDownloadInfo.setFinish(false);
        mFile = new File(filePath);
        mMD5 = md5;
        mRefreshTime = refreshTime;
        mBreakpointLength = breakpointLength;
        mIsBreakpoint = isBreakpoint;
        mListener = listener;

        QuickUtils.runOnUiThread(mListener != null, new Runnable() {
            @Override
            public void run() {
                if (isBindLife()) {
                    if (HttpLifecycleManager.isLifecycleActive(getLifecycleOwner())) {
                        mListener.onStart(getCall());
                    }
                } else {
                    mListener.onStart(getCall());
                }
            }
        });
    }

    @Override
    protected void onResponse(final Response response) throws Exception {
        //>>>获取响应头中的文件MD5值
        if (TextUtils.isEmpty(mMD5)) {
            String md5 = QuickUtils.getContentMD5(response);
            //这个MD5值必须是文件的MD5值
            if (!TextUtils.isEmpty(md5) && md5.matches(FILE_MD5_REGEX)) {
                mMD5 = md5;
            }
        }
        QuickLogUtils.i(TAG + "md5：" + mMD5);
        //>>>下载前校验文件
        if (QuickUtils.checkFileSafe(mMD5, mFile)) {
            //如果这个文件已经下载过，并且经过校验MD5是同一个文件的话，就直接回调下载成功监听
            QuickLogUtils.i(TAG + "文件已经下载过");
            downloadSuccess();
            return;
        }
        //>>>开始处理响应体
        if (response.body() == null) {
            throw new NullBodyException("The response body == null");
        }
        if (!response.isSuccessful()) {
            //1、HTTP 416 Range Not Satisfiable：原因是断点续传范围超出了实际文件长度范围，当文件已经下载完成了，但是又再次调用了下载就会出现这种情况。所以需要在下载前判断文件是否下载完成，一般是通过文件MD5值判断。
            throw new ResponseException("response is unsuccessful，code：" + response.code() + "，message：" + response.message(), response);
        }
        //>>>获取文件contentLength值：获取的长度是剩下需要下载的总长度
        long contentLength = QuickUtils.getContentLength(response);
        if (contentLength < 0) {
            contentLength = 0;
        }
        QuickLogUtils.i(TAG + "contentLength：" + contentLength);
        //>>>文件是否追加，是否真正进行断点续传
        boolean append = contentLength > 0 && mIsBreakpoint;
        QuickLogUtils.i(TAG + "isBreakpoint：" + append);
        //>>>获取断点文件长度
        long breakpointLength = 0;
        if (append) {
            breakpointLength = mBreakpointLength;
        } else {
            breakpointLength = 0;
        }
        QuickLogUtils.i(TAG + "breakpointLength：" + breakpointLength);
        //>>>设置文件总长度
        mDownloadInfo.setTotalLength(contentLength + breakpointLength);
        //>>>开始写入文件
        int readLength = 0;//读取长度
        byte[] bytes = new byte[8192];
        long downloadSize = 0;//当前下载长度
        InputStream inputStream = response.body().byteStream();//拿到输入流
        FileOutputStream outputStream = new FileOutputStream(mFile, append);//拿到文件输出流
        while ((readLength = inputStream.read(bytes)) != -1) {//循环遍历读写文件
            if (downloadSize == 0) {
                downloadSize += (readLength + breakpointLength);
            } else {
                downloadSize += readLength;
            }
            outputStream.write(bytes, 0, readLength);
            downloading(downloadSize);
        }
        outputStream.flush();
        //>>>最后校验文件
        String fileMD5 = QuickUtils.getFileMD5(mFile);
        if (!TextUtils.isEmpty(mMD5) && !mMD5.equalsIgnoreCase(fileMD5)) {
            QuickLogUtils.i(TAG + "文件MD5值校验失败：" + mMD5 + "<--->" + fileMD5);
            //文件MD5值校验失败
            throw new MD5Exception("file md5 verify failure", fileMD5);
        }
        downloadSuccess();
        //>>>关闭流
        QuickUtils.closeStream(inputStream);
        QuickUtils.closeStream(outputStream);
    }

    //下载中
    private void downloading(long downloadSize) {
        mDownloadInfo.setDownloadLength(downloadSize);
        mDownloadInfo.setFinish(false);

        final long currentTime = SystemClock.elapsedRealtime();
        final double currentProgress = mDownloadInfo.getPreciseProgress();
        if (currentTime - lastRefreshTime >= mRefreshTime && currentProgress != lastProgress) {//避免短时间内的频繁回调和相同进度重复回调
            QuickUtils.runOnUiThread(mListener != null, new Runnable() {
                @Override
                public void run() {
                    if (isBindLife()) {
                        if (HttpLifecycleManager.isLifecycleActive(getLifecycleOwner())) {
                            mListener.onProgress(mDownloadInfo);
                        }
                    } else {
                        mListener.onProgress(mDownloadInfo);
                    }
                }
            });
            lastRefreshTime = currentTime;
            lastProgress = currentProgress;
            QuickLogUtils.i(TAG + "下载中：" + mDownloadInfo.toString());
        }
    }

    //下载成功
    private void downloadSuccess() {
        mDownloadInfo.setDownloadLength(mDownloadInfo.getTotalLength());
        mDownloadInfo.setFinish(true);
        QuickUtils.runOnUiThread(mListener != null, new Runnable() {
            @Override
            public void run() {
                if (isBindLife()) {
                    if (HttpLifecycleManager.isLifecycleActive(getLifecycleOwner())) {
                        mListener.onProgress(mDownloadInfo);
                        mListener.onComplete(mDownloadInfo);
                        mListener.onEnd(getCall());
                    }
                } else {
                    mListener.onProgress(mDownloadInfo);
                    mListener.onComplete(mDownloadInfo);
                    mListener.onEnd(getCall());
                }
                QuickLogUtils.i(TAG + "下载完成：" + mDownloadInfo.toString());
            }
        });
    }

    @Override
    protected void onFailure(final Exception e) {
        QuickUtils.runOnUiThread(mListener != null, new Runnable() {
            @Override
            public void run() {
                if (isBindLife()) {
                    if (HttpLifecycleManager.isLifecycleActive(getLifecycleOwner())) {
                        mListener.onError(mDownloadInfo, e);
                        mListener.onEnd(getCall());
                    }
                } else {
                    mListener.onError(mDownloadInfo, e);
                    mListener.onEnd(getCall());
                }
                QuickLogUtils.i(TAG + "下载失败：" + mDownloadInfo.toString() + " error：" + e.getMessage());
            }
        });
    }

}