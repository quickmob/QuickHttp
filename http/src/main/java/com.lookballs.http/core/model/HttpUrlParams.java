package com.lookballs.http.core.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 请求参数封装
 */
public class HttpUrlParams {

    private Map<String, Object> mUrlParams = null;

    public void put(String key, Object value) {
        if (key != null && value != null) {
            if (mUrlParams == null) {
                mUrlParams = new HashMap<>();
            }
            mUrlParams.put(key, value);
        }
    }

    public void putAll(Map<String, Object> params) {
        if (params != null && !params.isEmpty()) {
            if (mUrlParams == null) {
                mUrlParams = new HashMap<>();
            }
            mUrlParams.putAll(params);
        }
    }

    public Object getValue(String key) {
        if (mUrlParams != null) {
            return mUrlParams.get(key);
        } else {
            return null;
        }
    }

    public boolean isEmpty() {
        return mUrlParams == null || mUrlParams.isEmpty();
    }

    public Set<String> getKeys() {
        if (mUrlParams != null) {
            return mUrlParams.keySet();
        } else {
            return null;
        }
    }

    public Map<String, Object> getParams() {
        return mUrlParams;
    }
}