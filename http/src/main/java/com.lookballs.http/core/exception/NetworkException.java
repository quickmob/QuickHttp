package com.lookballs.http.core.exception;

/**
 * 网络连接异常
 */
public final class NetworkException extends HttpException {

    public NetworkException(String message) {
        super(message);
    }

    public NetworkException(String message, Throwable cause) {
        super(message, cause);
    }
}