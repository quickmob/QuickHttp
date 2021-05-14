package com.lookballs.http.internal.callback;

import androidx.lifecycle.LifecycleOwner;

import com.lookballs.http.QuickHttp;
import com.lookballs.http.core.lifecycle.HttpLifecycleManager;
import com.lookballs.http.core.model.HttpCall;
import com.lookballs.http.listener.OnRetryConditionListener;
import com.lookballs.http.utils.QuickLogUtils;
import com.lookballs.http.utils.QuickUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 接口回调基类
 */
public abstract class BaseCallback implements Callback {

    private final HttpCall mHttpCall;//请求任务对象
    private final int mRetryCount;//设置重试次数
    private final long mRetryDelayMillis;//设置重试延迟时间
    private final LifecycleOwner mLifecycleOwner;//生命周期管理
    private final OnRetryConditionListener mOnRetryConditionListener;//设置重试条件

    private int mCurrentRetryCount;//当前重试次数

    public BaseCallback(LifecycleOwner lifecycleOwner, HttpCall call, int retryCount, long retryDelayMillis, OnRetryConditionListener onRetryConditionListener) {
        mLifecycleOwner = lifecycleOwner;
        HttpLifecycleManager.bind(lifecycleOwner);
        mHttpCall = call;
        if (retryCount > 0) {
            mRetryCount = retryCount;
        } else {
            mRetryCount = QuickHttp.getConfig().getRetryCount();
        }
        if (retryDelayMillis > 0) {
            mRetryDelayMillis = retryDelayMillis;
        } else {
            mRetryDelayMillis = QuickHttp.getConfig().getRetryDelayMillis();
        }
        if (onRetryConditionListener != null) {
            mOnRetryConditionListener = onRetryConditionListener;
        } else {
            mOnRetryConditionListener = QuickHttp.getConfig().getOnRetryConditionListener();
        }
    }

    public LifecycleOwner getLifecycleOwner() {
        return mLifecycleOwner;
    }

    public HttpCall getCall() {
        return mHttpCall;
    }

    @Override
    public void onResponse(final Call call, final Response response) {
        try {
            if (!call.isCanceled()) {
                if (response.body() != null) {
                    onResponse(response);
                } else {
                    onFailure(new Exception("The response body == null"));
                }
            } else {
                QuickLogUtils.i("onResponse call isCanceled");
            }
        } catch (Exception e) {
            onFailure(e);
        } finally {
            response.close();
        }
    }

    @Override
    public void onFailure(final Call call, final IOException e) {
        boolean retryCondition = defaultRetryCondition(e);
        if (mOnRetryConditionListener != null) {
            retryCondition = mOnRetryConditionListener.retryCondition(e);
        }
        if (retryCondition) {
            if (mCurrentRetryCount < mRetryCount) {
                if (mRetryDelayMillis > 0) {
                    QuickUtils.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            retryRequest(call);
                        }
                    }, mRetryDelayMillis);
                } else {
                    retryRequest(call);
                }
                return;
            }
        }
        onFailure(e);
    }

    private void retryRequest(Call call) {
        if (HttpLifecycleManager.isLifecycleActive(getLifecycleOwner())) {
            mCurrentRetryCount++;
            Call newCall = call.clone();
            mHttpCall.setCall(newCall);
            newCall.enqueue(BaseCallback.this);
            QuickLogUtils.i("延时" + mRetryDelayMillis + "毫秒后进行重试，当前重试次数：" + mCurrentRetryCount);
        } else {
            QuickLogUtils.i("宿主已被销毁，无法进行重试");
        }
    }

    private boolean defaultRetryCondition(Exception e) {
        if (e instanceof SocketTimeoutException) {
            return true;
        } else {
            return false;
        }
    }

    protected abstract void onResponse(Response response) throws Exception;

    protected abstract void onFailure(Exception e);

}
