package com.lookballs.http.core.exception;

/**
 * 返回结果异常
 */
public final class ResultException extends HttpException {

    private final Object mData;

    public ResultException(String message, Object data) {
        super(message);
        mData = data;
    }

    public ResultException(String message, Throwable cause, Object data) {
        super(message, cause);
        mData = data;
    }

    @SuppressWarnings("unchecked")
    public <T extends Object> T getData() {
        return (T) mData;
    }
}