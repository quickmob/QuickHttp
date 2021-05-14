package com.lookballs.http.core.request;

import androidx.lifecycle.LifecycleOwner;

import com.lookballs.http.core.model.HttpMethod;

/**
 * head请求
 */
public class HeadRequest extends UrlRequest<HeadRequest> {

    private HeadRequest(LifecycleOwner lifecycleOwner) {
        super(lifecycleOwner);
    }

    public static HeadRequest with(LifecycleOwner lifecycleOwner) {
        return new HeadRequest(lifecycleOwner);
    }

    @Override
    protected String getRequestMethod() {
        return String.valueOf(HttpMethod.HEAD);
    }

}