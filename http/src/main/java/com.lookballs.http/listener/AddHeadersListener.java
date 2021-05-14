package com.lookballs.http.listener;

import java.util.Map;

/**
 * 添加公共请求头
 */
public interface AddHeadersListener {
    Map<String, String> applyMap();
}