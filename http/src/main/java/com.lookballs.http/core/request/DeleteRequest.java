package com.lookballs.http.core.request;

import androidx.lifecycle.LifecycleOwner;

import com.lookballs.http.core.model.HttpMethod;

/**
 * delete请求
 */
public class DeleteRequest extends BodyRequest<DeleteRequest> {

    private DeleteRequest(LifecycleOwner lifecycleOwner) {
        super(lifecycleOwner);
    }

    public static DeleteRequest with(LifecycleOwner lifecycleOwner) {
        return new DeleteRequest(lifecycleOwner);
    }

    @Override
    protected String getRequestMethod() {
        return String.valueOf(HttpMethod.DELETE);
    }

}