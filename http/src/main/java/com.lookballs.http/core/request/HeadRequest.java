package com.lookballs.http.core.request;

import com.lookballs.http.core.model.HttpMethod;

/**
 * head请求
 */
public class HeadRequest extends UrlRequest<HeadRequest> {

    private HeadRequest(String url) {
        super(url);
    }

    public static HeadRequest with(String url) {
        return new HeadRequest(url);
    }

    @Override
    protected String getRequestMethod() {
        return String.valueOf(HttpMethod.HEAD);
    }

}