package com.lookballs.http.core.request;

import com.lookballs.http.internal.define.HttpMethod;

/**
 * post请求
 */
public class PostRequest extends BaseBodyRequest<PostRequest> {

    private PostRequest(String url) {
        super(url);
    }

    public static PostRequest with(String url) {
        return new PostRequest(url);
    }

    @Override
    protected String getRequestMethod() {
        return String.valueOf(HttpMethod.POST);
    }

}