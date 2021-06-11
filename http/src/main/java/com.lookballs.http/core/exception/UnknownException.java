package com.lookballs.http.core.exception;

/**
 * 未知异常
 */
public final class UnknownException extends HttpException {

    public UnknownException(String message) {
        super(message);
    }

    public UnknownException(String message, Throwable cause) {
        super(message, cause);
    }
}