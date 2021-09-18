package com.lookballs.http.core.listener;

/**
 * 请求重试条件回调
 */
public interface OnRetryConditionListener {
    boolean retryCondition(Exception e);
}