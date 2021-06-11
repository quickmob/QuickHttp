package com.lookballs.http.core.request;

import com.lookballs.http.core.model.HttpMethod;

/**
 * patch请求
 */
public class PatchRequest extends BodyRequest<PatchRequest> {

    private PatchRequest(String url) {
        super(url);
    }

    public static PatchRequest with(String url) {
        return new PatchRequest(url);
    }

    @Override
    protected String getRequestMethod() {
        return String.valueOf(HttpMethod.PATCH);
    }

}