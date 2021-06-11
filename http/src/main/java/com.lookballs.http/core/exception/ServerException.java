package com.lookballs.http.core.exception;

/**
 * 服务器连接异常
 */
public final class ServerException extends HttpException {

    public ServerException(String message) {
        super(message);
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }
}