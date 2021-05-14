package com.lookballs.http.internal.body;

import android.os.SystemClock;

import androidx.lifecycle.LifecycleOwner;

import com.lookballs.http.core.lifecycle.HttpLifecycleManager;
import com.lookballs.http.core.model.UploadInfo;
import com.lookballs.http.listener.OnUploadListener;
import com.lookballs.http.utils.QuickLogUtils;
import com.lookballs.http.utils.QuickUtils;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * RequestBody代理类（用于获取上传进度）
 */
public final class ProgressBody extends RequestBody {

    private final RequestBody mRequestBody;//RequestBody
    private final UploadInfo mUploadInfo;//上传进度信息
    private final LifecycleOwner mLifecycleOwner;//LifecycleOwner
    private final OnUploadListener mListener;//上传回调
    private final long mRefreshTime;//上传回调进度刷新时间

    private long mTotalByte;//总字节数
    private long mUploadByte;//已上传字节数
    private int lastProgress;//最后一次刷新的进度
    private long lastRefreshTime = 0L;//最后一次刷新进度的时间

    public ProgressBody(RequestBody body, LifecycleOwner lifecycleOwner, OnUploadListener listener, long refreshTime) {
        mRequestBody = body;
        mUploadInfo = new UploadInfo();
        mLifecycleOwner = lifecycleOwner;
        mListener = listener;
        mRefreshTime = refreshTime;
    }

    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mRequestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        mTotalByte = contentLength();
        mUploadInfo.setTotalLength(mTotalByte);
        mUploadInfo.setRefreshTime(mRefreshTime);

        sink = Okio.buffer(new WrapperSink(sink));
        mRequestBody.writeTo(sink);
        sink.flush();
    }

    private class WrapperSink extends ForwardingSink {

        public WrapperSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            mUploadByte += byteCount;
            mUploadInfo.setUploadLength(mUploadByte);

            QuickUtils.runOnUiThread(mListener != null, new Runnable() {
                @Override
                public void run() {
                    final long currentTime = SystemClock.elapsedRealtime();
                    final int currentProgress = mUploadInfo.getProgress();
                    if (HttpLifecycleManager.isLifecycleActive(mLifecycleOwner)) {
                        if (currentTime - lastRefreshTime >= mRefreshTime && currentProgress != lastProgress) {//避免短时间内的频繁回调和相同进度重复回调
                            mListener.onProgress(mUploadInfo);
                        }
                    }
                    lastRefreshTime = currentTime;
                    lastProgress = currentProgress;
                    QuickLogUtils.i("UploadCallback>>>" +
                            "总字节：" + mTotalByte +
                            " 已上传字节：" + mUploadByte +
                            " 上传进度：" + currentProgress + "%");
                }
            });
        }
    }

}