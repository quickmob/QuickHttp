package com.lookballs.http.core.listener;

import java.util.Map;

/**
 * 添加url公共拼接参数
 */
public interface AddUrlParamsListener {
    Map<String, Object> applyMap();
}