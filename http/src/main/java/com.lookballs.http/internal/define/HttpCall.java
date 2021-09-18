package com.lookballs.http.internal.define;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okio.Timeout;

/**
 * 请求对象代理
 */
public class HttpCall implements Call {

    private Call mCall;

    public HttpCall(Call call) {
        mCall = call;
    }

    public void setCall(Call call) {
        mCall = call;
    }

    @Override
    public Request request() {
        if (mCall != null) {
            return mCall.request();
        }
        return null;
    }

    @Override
    public Response execute() throws IOException {
        if (mCall != null) {
            return mCall.execute();
        }
        return null;
    }

    @Override
    public void enqueue(Callback responseCallback) {
        if (mCall != null) {
            mCall.enqueue(responseCallback);
        }
    }

    @Override
    public void cancel() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    @Override
    public boolean isExecuted() {
        if (mCall != null) {
            return mCall.isExecuted();
        }
        return false;
    }

    @Override
    public boolean isCanceled() {
        if (mCall != null) {
            return mCall.isCanceled();
        }
        return false;
    }

    @Override
    public Timeout timeout() {
        if (mCall != null) {
            return mCall.timeout();
        }
        return null;
    }

    @Override
    public Call clone() {
        if (mCall != null) {
            return mCall.clone();
        }
        return null;
    }

}