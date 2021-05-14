package com.lookballs.http.core.request;

import androidx.lifecycle.LifecycleOwner;

import com.lookballs.http.core.model.HttpMethod;

/**
 * put请求
 */
public class PutRequest extends BodyRequest<PutRequest> {

    private PutRequest(LifecycleOwner lifecycleOwner) {
        super(lifecycleOwner);
    }

    public static PutRequest with(LifecycleOwner lifecycleOwner) {
        return new PutRequest(lifecycleOwner);
    }

    @Override
    protected String getRequestMethod() {
        return String.valueOf(HttpMethod.PUT);
    }

}