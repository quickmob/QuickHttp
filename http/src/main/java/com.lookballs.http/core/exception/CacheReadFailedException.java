package com.lookballs.http.core.exception;

import java.io.IOException;

/**
 * 缓存读取失败异常
 */
public class CacheReadFailedException extends IOException {

    public CacheReadFailedException(String message) {
        super(message);
    }

    public CacheReadFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}