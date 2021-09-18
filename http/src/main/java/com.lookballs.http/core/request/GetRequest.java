package com.lookballs.http.core.request;

import com.lookballs.http.internal.define.HttpMethod;

/**
 * get请求
 */
public class GetRequest extends BaseUrlRequest<GetRequest> {

    private GetRequest(String url) {
        super(url);
    }

    public static GetRequest with(String url) {
        return new GetRequest(url);
    }

    @Override
    protected String getRequestMethod() {
        return String.valueOf(HttpMethod.GET);
    }

}