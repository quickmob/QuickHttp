package com.lookballs.http.core.request;

import androidx.lifecycle.LifecycleOwner;

import com.lookballs.http.core.model.HttpMethod;

/**
 * get请求
 */
public class GetRequest extends UrlRequest<GetRequest> {

    private GetRequest(LifecycleOwner lifecycleOwner) {
        super(lifecycleOwner);
    }

    public static GetRequest with(LifecycleOwner lifecycleOwner) {
        return new GetRequest(lifecycleOwner);
    }

    @Override
    protected String getRequestMethod() {
        return String.valueOf(HttpMethod.GET);
    }

}