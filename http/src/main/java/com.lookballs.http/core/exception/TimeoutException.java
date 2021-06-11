package com.lookballs.http.core.exception;

/**
 * 服务器超时异常
 */
public final class TimeoutException extends HttpException {

    public TimeoutException(String message) {
        super(message);
    }

    public TimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}