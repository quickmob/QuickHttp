package com.lookballs.http.listener;

import okhttp3.Call;

/**
 * 请求回调包装类
 */
public class HttpCallback<T> implements OnHttpListener<T> {

    private final OnHttpListener mListener;

    public HttpCallback(OnHttpListener listener) {
        mListener = listener;
    }

    @Override
    public void onStart(Call call) {
        if (mListener != null) {
            mListener.onStart(call);
        }
    }

    @Override
    public void onSucceed(T result) {
        if (mListener != null) {
            mListener.onSucceed(result);
        }
    }

    @Override
    public void onError(int code, Exception e) {
        if (mListener != null) {
            mListener.onError(code, e);
        }
    }

    @Override
    public void onEnd(Call call) {
        if (mListener != null) {
            mListener.onEnd(call);
        }
    }
}
