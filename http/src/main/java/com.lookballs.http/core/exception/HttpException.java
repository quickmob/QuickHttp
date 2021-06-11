package com.lookballs.http.core.exception;

/**
 * 网络请求异常
 */
public class HttpException extends Exception {

    private String mMessage;

    public HttpException(String message) {
        super(message);
        mMessage = message;
    }

    public HttpException(String message, Throwable cause) {
        super(message, cause);
        mMessage = message;
    }

    /**
     * 获取错误信息
     */
    @Override
    public String getMessage() {
        return mMessage;
    }
}