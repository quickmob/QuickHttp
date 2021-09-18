package com.lookballs.http.core.request;

import com.lookballs.http.core.BodyType;
import com.lookballs.http.core.listener.OnDownloadListener;
import com.lookballs.http.core.listener.OnHttpListener;
import com.lookballs.http.core.utils.QuickUtils;
import com.lookballs.http.internal.callback.DownloadCallback;
import com.lookballs.http.internal.define.HttpCall;
import com.lookballs.http.internal.define.HttpHeaders;
import com.lookballs.http.internal.define.HttpMethod;
import com.lookballs.http.internal.define.HttpParams;
import com.lookballs.http.internal.define.HttpUrlParams;

import java.io.File;

import okhttp3.Request;

/**
 * 下载请求
 */
public class DownloadRequest extends BaseRequest<DownloadRequest> {

    private HttpMethod mMethod = HttpMethod.GET;//下载方式
    private String mFilePath;//保存的文件路径
    private String mMD5;//校验的MD5
    private long mRefreshTime = 100;//下载回调进度刷新时间，默认100毫秒
    private long mBreakpointLength = 0;//断点续传下载起始位置
    private boolean mIsBreakpoint = false;//是否开启断点续传下载

    private DownloadRequest(String url) {
        super(url);
    }

    public static DownloadRequest with(String url) {
        return new DownloadRequest(url);
    }

    //设置请求方式
    public DownloadRequest method(HttpMethod method) {
        mMethod = method;
        return this;
    }

    //设置保存的文件
    public DownloadRequest file(File file) {
        if (file != null) {
            mFilePath = file.getAbsolutePath();
        }
        return this;
    }

    //设置保存的文件路径
    public DownloadRequest filePath(String filePath) {
        mFilePath = filePath;
        return this;
    }

    //设置MD5值
    public DownloadRequest fileMd5(String fileMd5) {
        mMD5 = fileMd5;
        return this;
    }

    //设置下载回调进度刷新时间，默认100毫秒
    public DownloadRequest refreshTime(long refreshTime) {
        if (refreshTime < 0) {
            refreshTime = 0;
        }
        mRefreshTime = refreshTime;
        return this;
    }

    //设置是否开启断点续传
    public DownloadRequest breakpoint(long breakpointLength, boolean isBreakpoint) {
        if (breakpointLength < 0) {
            breakpointLength = 0;
        }
        mBreakpointLength = breakpointLength;
        mIsBreakpoint = isBreakpoint;
        return this;
    }

    //开始下载请求
    public void start(OnDownloadListener listener) {
        if (mDelayMillis > 0) {
            QuickUtils.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startRequest(listener);
                }
            }, mDelayMillis);
        } else {
            startRequest(listener);
        }
    }

    @Override
    public <B> B sync(Class<B> clazz) throws Exception {
        //请调用start方法
        throw new IllegalStateException(">>>Can't use this method at download request");
    }

    public void async(OnHttpListener listener) {
        //请调用start方法
        throw new IllegalStateException(">>>Can't use this method at download request");
    }

    @Override
    protected String getRequestMethod() {
        return String.valueOf(mMethod);
    }

    @Override
    protected Request createRequest(String url, Object tag, HttpHeaders headers, HttpUrlParams urlParams, HttpParams params, BodyType bodyType) {
        if (mIsBreakpoint) {
            //设置断点续传
            QuickUtils.setRangeHeader(headers, mBreakpointLength);
        }
        switch (mMethod) {
            case HEAD:
                return HeadRequest.with(url).createRequest(url, tag, headers, urlParams, params, bodyType);
            case POST:
                return PostRequest.with(url).createRequest(url, tag, headers, urlParams, params, bodyType);
            case DELETE:
                return DeleteRequest.with(url).createRequest(url, tag, headers, urlParams, params, bodyType);
            case PATCH:
                return PatchRequest.with(url).createRequest(url, tag, headers, urlParams, params, bodyType);
            case PUT:
                return PutRequest.with(url).createRequest(url, tag, headers, urlParams, params, bodyType);
            default:
                return GetRequest.with(url).createRequest(url, tag, headers, urlParams, params, bodyType);
        }
    }

    //创建下载请求
    private void startRequest(OnDownloadListener listener) {
        mHttpCall = new HttpCall(createCall());
        mHttpCall.enqueue(new DownloadCallback(getLifecycleOwner(), isBindLife(), mHttpCall, mRetryCount, mRetryDelayMillis, mOnRetryConditionListener, mFilePath, mMD5, mRefreshTime, mBreakpointLength, mIsBreakpoint, listener));
    }
}