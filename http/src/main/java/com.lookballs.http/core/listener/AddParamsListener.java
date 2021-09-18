package com.lookballs.http.core.listener;

import java.util.Map;

/**
 * 添加公共参数
 */
public interface AddParamsListener {
    Map<String, Object> applyMap();
}