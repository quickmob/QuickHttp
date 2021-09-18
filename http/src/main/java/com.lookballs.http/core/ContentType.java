package com.lookballs.http.core;

import okhttp3.MediaType;

/**
 * 内容类型
 */
public final class ContentType {

    /**
     * 字节流
     */
    public static final MediaType STREAM = MediaType.parse("application/octet-stream");

    /**
     * Json
     */
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * 纯文本
     */
    public static final MediaType TEXT = MediaType.parse("text/plain; charset=utf-8");
}