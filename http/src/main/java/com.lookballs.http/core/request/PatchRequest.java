package com.lookballs.http.core.request;

import androidx.lifecycle.LifecycleOwner;

import com.lookballs.http.core.model.HttpMethod;

/**
 * patch请求
 */
public class PatchRequest extends BodyRequest<PatchRequest> {

    private PatchRequest(LifecycleOwner lifecycleOwner) {
        super(lifecycleOwner);
    }

    public static PatchRequest with(LifecycleOwner lifecycleOwner) {
        return new PatchRequest(lifecycleOwner);
    }

    @Override
    protected String getRequestMethod() {
        return String.valueOf(HttpMethod.PATCH);
    }

}