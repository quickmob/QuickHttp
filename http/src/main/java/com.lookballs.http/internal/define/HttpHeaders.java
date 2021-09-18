package com.lookballs.http.internal.define;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 请求头封装
 */
public class HttpHeaders {

    private Map<String, String> mHeaders = null;

    public void put(String key, String value) {
        if (key != null && value != null) {
            if (mHeaders == null) {
                mHeaders = new HashMap<>();
            }
            mHeaders.put(key, value);
        }
    }

    public void putAll(Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            if (mHeaders == null) {
                mHeaders = new HashMap<>();
            }
            mHeaders.putAll(params);
        }
    }

    public String getValue(String key) {
        if (mHeaders != null) {
            return mHeaders.get(key);
        } else {
            return null;
        }
    }

    public boolean isEmpty() {
        return mHeaders == null || mHeaders.isEmpty();
    }

    public Set<String> getKeys() {
        if (mHeaders != null) {
            return mHeaders.keySet();
        } else {
            return null;
        }
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }
}