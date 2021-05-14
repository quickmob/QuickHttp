package com.lookballs.http.core.request;

import androidx.lifecycle.LifecycleOwner;

import com.lookballs.http.core.model.HttpMethod;

/**
 * post请求
 */
public class PostRequest extends BodyRequest<PostRequest> {

    private PostRequest(LifecycleOwner lifecycleOwner) {
        super(lifecycleOwner);
    }

    public static PostRequest with(LifecycleOwner lifecycleOwner) {
        return new PostRequest(lifecycleOwner);
    }

    @Override
    protected String getRequestMethod() {
        return String.valueOf(HttpMethod.POST);
    }

}