package com.lookballs.http.core.cache;

/**
 * 缓存模式
 */
public enum CacheMode {

    /**
     * 仅请求网络，默认模式(不写缓存)
     */
    ONLY_REQUEST_NETWORK,

    /**
     * 仅读取缓存(不写缓存)
     */
    ONLY_READ_CACHE,

    /**
     * 请求成功后，写入缓存
     * 跟{@link #ONLY_REQUEST_NETWORK}默认模式相比，仅多了写缓存的操作
     */
    REQUEST_NETWORK_SUCCESS_WRITE_CACHE,

    /**
     * 先读取缓存，失败后再请求网络(网络请求成功，写缓存)
     */
    READ_CACHE_FAILED_REQUEST_NETWORK,

    /**
     * 先请求网络，失败后再读取缓存(网络请求成功，写缓存)
     */
    REQUEST_NETWORK_FAILED_READ_CACHE
}