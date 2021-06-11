package com.lookballs.http.core.request;

import com.lookballs.http.core.model.HttpMethod;

/**
 * delete请求
 */
public class DeleteRequest extends BodyRequest<DeleteRequest> {

    private DeleteRequest(String url) {
        super(url);
    }

    public static DeleteRequest with(String url) {
        return new DeleteRequest(url);
    }

    @Override
    protected String getRequestMethod() {
        return String.valueOf(HttpMethod.DELETE);
    }

}