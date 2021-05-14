package com.lookballs.http.internal.callback;

import androidx.lifecycle.LifecycleOwner;

import com.lookballs.http.QuickHttp;
import com.lookballs.http.core.converter.IDataConverter;
import com.lookballs.http.core.lifecycle.HttpLifecycleManager;
import com.lookballs.http.core.model.HttpCall;
import com.lookballs.http.internal.GsonPreconditions;
import com.lookballs.http.internal.GsonTypes;
import com.lookballs.http.listener.OnHttpListener;
import com.lookballs.http.listener.OnRetryConditionListener;
import com.lookballs.http.utils.QuickUtils;

import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * 正常接口回调
 */
public final class NormalCallback extends BaseCallback {

    private final OnHttpListener mListener;//监听回调
    private final Class mClazz;//需要转换成数据的类
    private final IDataConverter mDataConverter;//数据转换器

    public NormalCallback(LifecycleOwner lifecycleOwner, HttpCall call, int retryCount, long retryDelayMillis, OnRetryConditionListener onRetryConditionListener, OnHttpListener listener, IDataConverter dataConverter) {
        this(lifecycleOwner, call, retryCount, retryDelayMillis, onRetryConditionListener, listener, null, dataConverter);
    }

    public NormalCallback(LifecycleOwner lifecycleOwner, HttpCall call, int retryCount, long retryDelayMillis, OnRetryConditionListener onRetryConditionListener, OnHttpListener listener, Class clazz, IDataConverter dataConverter) {
        super(lifecycleOwner, call, retryCount, retryDelayMillis, onRetryConditionListener);
        mListener = listener;
        mClazz = clazz;
        mDataConverter = dataConverter;

        QuickUtils.runOnUiThread(mListener != null, new Runnable() {
            @Override
            public void run() {
                if (HttpLifecycleManager.isLifecycleActive(getLifecycleOwner())) {
                    mListener.onStart(getCall());
                }
            }
        });
    }

    @Override
    protected void onResponse(final Response response) throws Exception {
        if (response.isSuccessful()) {
            onSucceed(response);
        } else {
            onFail(response.code(), new Exception(response.message()));
        }
    }

    private void onSucceed(final Response response) throws Exception {
        Type type = null;
        if (mClazz != null) {
            type = GsonTypes.canonicalize(GsonPreconditions.checkNotNull(mClazz));
        } else {
            type = QuickUtils.getParameterizedType(mListener);
        }

        Object result = null;
        if (mDataConverter != null) {
            result = mDataConverter.onSucceed(getLifecycleOwner(), response, type);
        } else {
            result = QuickHttp.getConfig().getDataConverter().onSucceed(getLifecycleOwner(), response, type);
        }
        Object finalResult = result;
        QuickUtils.runOnUiThread(mListener != null, new Runnable() {
            @Override
            public void run() {
                if (HttpLifecycleManager.isLifecycleActive(getLifecycleOwner())) {
                    mListener.onSucceed(finalResult);
                    mListener.onEnd(getCall());
                }
            }
        });
    }

    @Override
    protected void onFailure(final Exception e) {
        onFail(QuickHttp.ERROR_CODE, e);
    }

    private void onFail(final int code, final Exception e) {
        Exception exception = null;
        if (mDataConverter != null) {
            exception = mDataConverter.onFail(getLifecycleOwner(), e);
        } else {
            exception = QuickHttp.getConfig().getDataConverter().onFail(getLifecycleOwner(), e);
        }
        Exception finalException = exception;
        QuickUtils.runOnUiThread(mListener != null, new Runnable() {
            @Override
            public void run() {
                if (HttpLifecycleManager.isLifecycleActive(getLifecycleOwner())) {
                    mListener.onError(code, finalException);
                    mListener.onEnd(getCall());
                }
            }
        });
    }

}