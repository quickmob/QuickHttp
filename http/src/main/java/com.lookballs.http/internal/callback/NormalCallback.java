package com.lookballs.http.internal.callback;

import androidx.lifecycle.LifecycleOwner;

import com.lookballs.http.QuickHttp;
import com.lookballs.http.core.converter.IDataConverter;
import com.lookballs.http.core.exception.NullBodyException;
import com.lookballs.http.core.exception.ResponseException;
import com.lookballs.http.core.lifecycle.HttpLifecycleManager;
import com.lookballs.http.core.listener.OnHttpListener;
import com.lookballs.http.core.listener.OnRetryConditionListener;
import com.lookballs.http.core.utils.QuickUtils;
import com.lookballs.http.internal.GsonPreconditions;
import com.lookballs.http.internal.GsonTypes;
import com.lookballs.http.internal.define.HttpCall;

import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * 正常接口回调
 */
public final class NormalCallback extends BaseCallback {

    private final String mUrl;//请求url
    private final OnHttpListener mListener;//监听回调
    private final Class mClazz;//需要转换成数据的类
    private final IDataConverter mDataConverter;//数据转换器

    public NormalCallback(LifecycleOwner lifecycleOwner, boolean isBindLife, HttpCall call, int retryCount, long retryDelayMillis, String url, OnRetryConditionListener onRetryConditionListener, OnHttpListener listener, IDataConverter dataConverter) {
        this(lifecycleOwner, isBindLife, call, retryCount, retryDelayMillis, url, onRetryConditionListener, listener, null, dataConverter);
    }

    public NormalCallback(LifecycleOwner lifecycleOwner, boolean isBindLife, HttpCall call, int retryCount, long retryDelayMillis, String url, OnRetryConditionListener onRetryConditionListener, OnHttpListener listener, Class clazz, IDataConverter dataConverter) {
        super(lifecycleOwner, isBindLife, call, retryCount, retryDelayMillis, onRetryConditionListener);
        mUrl = url;
        mListener = listener;
        mClazz = clazz;
        mDataConverter = dataConverter;

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
        if (response.body() == null) {
            throw new NullBodyException("The response body == null");
        }
        if (!response.isSuccessful()) {
            throw new ResponseException("response is unsuccessful，code：" + response.code() + "，message：" + response.message(), response);
        }
        onSucceed(response);
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
            result = mDataConverter.onSucceed(getLifecycleOwner(), mUrl, response, type);
        } else {
            result = QuickHttp.getConfig().getDataConverter().onSucceed(getLifecycleOwner(), mUrl, response, type);
        }
        Object finalResult = result;
        QuickUtils.runOnUiThread(mListener != null, new Runnable() {
            @Override
            public void run() {
                if (isBindLife()) {
                    if (HttpLifecycleManager.isLifecycleActive(getLifecycleOwner())) {
                        mListener.onSucceed(finalResult);
                        mListener.onEnd(getCall());
                    }
                } else {
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
            exception = mDataConverter.onFail(getLifecycleOwner(), mUrl, e);
        } else {
            exception = QuickHttp.getConfig().getDataConverter().onFail(getLifecycleOwner(), mUrl, e);
        }
        Exception finalException = exception;
        QuickUtils.runOnUiThread(mListener != null, new Runnable() {
            @Override
            public void run() {
                if (isBindLife()) {
                    if (HttpLifecycleManager.isLifecycleActive(getLifecycleOwner())) {
                        mListener.onError(code, finalException);
                        mListener.onEnd(getCall());
                    }
                } else {
                    mListener.onError(code, finalException);
                    mListener.onEnd(getCall());
                }
            }
        });
    }

}