package com.lookballs.http.core.cache;

import androidx.annotation.Nullable;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * 缓存策略
 */
public interface ICacheStrategy {
    @Nullable
    Response get(Request request, String key) throws IOException;

    @Nullable
    Response put(Response response, String key) throws IOException;

    void remove(String key) throws IOException;

    void removeAll() throws IOException;
}
