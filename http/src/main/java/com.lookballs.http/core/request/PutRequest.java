package com.lookballs.http.core.request;

import com.lookballs.http.core.model.HttpMethod;

/**
 * put请求
 */
public class PutRequest extends BodyRequest<PutRequest> {

    private PutRequest(String url) {
        super(url);
    }

    public static PutRequest with(String url) {
        return new PutRequest(url);
    }

    @Override
    protected String getRequestMethod() {
        return String.valueOf(HttpMethod.PUT);
    }

}