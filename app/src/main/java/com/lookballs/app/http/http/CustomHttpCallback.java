package com.lookballs.app.http.http;

import com.lookballs.http.core.listener.OnHttpListener;

import okhttp3.Call;

/**
 * 自定义请求回调
 */
public class CustomHttpCallback<T> implements OnHttpListener<T> {

    private static final String TAG = "CustomHttpCallback";

    private OnHttpListener mListener;

    public CustomHttpCallback(OnHttpListener listener) {
        this.mListener = listener;
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
