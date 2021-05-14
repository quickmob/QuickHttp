package com.lookballs.http.core.model;

import com.lookballs.http.utils.QuickUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 请求参数封装
 */
public class HttpParams {

    private Map<String, Object> mParams = null;
    private boolean mMultipart;

    public void put(String key, Object value) {
        if (key != null && value != null) {
            if (mParams == null) {
                mParams = new HashMap<>();
            }
            mParams.put(key, value);
            if (QuickUtils.isMultipart(value)) {
                mMultipart = true;
            }
        }
    }

    public void putAll(Map<String, Object> params) {
        if (params != null && !params.isEmpty()) {
            if (mParams == null) {
                mParams = new HashMap<>();
            }
            mParams.putAll(params);
            for (String key : params.keySet()) {
                if (QuickUtils.isMultipart(params.get(key))) {
                    mMultipart = true;
                    break;
                }
            }
        }
    }

    public Object getValue(String key) {
        if (mParams != null) {
            return mParams.get(key);
        } else {
            return null;
        }
    }

    public boolean isEmpty() {
        return mParams == null || mParams.isEmpty();
    }

    public Set<String> getKeys() {
        if (mParams != null) {
            return mParams.keySet();
        } else {
            return null;
        }
    }

    public Map<String, Object> getParams() {
        return mParams;
    }

    public boolean isMultipart() {
        return mMultipart;
    }
}