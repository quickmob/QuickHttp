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
    private final boolean mBindLife;//是否绑定生命周期
    private final OnRetryConditionListener mOnRetryConditionListener;//设置重试条件

    private int mCurrentRetryCount;//当前重试次数

    public BaseCallback(LifecycleOwner lifecycleOwner, boolean isBindLife, HttpCall call, int retryCount, long retryDelayMillis, OnRetryConditionListener onRetryConditionListener) {
        mLifecycleOwner = lifecycleOwner;
        mBindLife = isBindLife;
        if (isBindLife()) {
            HttpLifecycleManager.bind(lifecycleOwner);
        }
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

    public boolean isBindLife() {
        return mBindLife;
    }

    public HttpCall getCall() {
        return mHttpCall;
    }

    @Override
    public void onResponse(final Call call, final Response response) {
        try {
            //收到响应
            onResponse(response);
        } catch (Exception e) {
            //回调失败
            onFailure(e);
        } finally {
            //关闭响应
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
        if (isBindLife()) {
            if (HttpLifecycleManager.isLifecycleActive(getLifecycleOwner())) {
                retryCall(call);
            } else {
                QuickLogUtils.i("宿主已被销毁，无法进行重试");
            }
        } else {
            retryCall(call);
        }
    }

    private void retryCall(Call call) {
        mCurrentRetryCount++;
        Call newCall = call.clone();
        mHttpCall.setCall(newCall);
        newCall.enqueue(BaseCallback.this);
        QuickLogUtils.i("延时" + mRetryDelayMillis + "毫秒后进行重试，当前重试次数：" + mCurrentRetryCount);
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
