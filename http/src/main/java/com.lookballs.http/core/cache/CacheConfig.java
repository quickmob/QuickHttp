package com.lookballs.http.core.cache;

/**
 * 缓存配置类
 */
public class CacheConfig {
    private String cacheKey;//缓存key
    private CacheMode cacheMode = CacheMode.ONLY_REQUEST_NETWORK;//缓存模式(默认不开启缓存)
    private long cacheValidTime = -1;//缓存有效时间(默认-1，代表永久有效)

    public CacheConfig(CacheConfig cacheConfig) {
        if (cacheConfig != null) {
            this.cacheValidTime = cacheConfig.cacheValidTime;
            this.cacheMode = cacheConfig.cacheMode;
        }
    }

    public CacheConfig(CacheMode cacheMode, long cacheValidTime) {
        this.cacheMode = cacheMode;
        this.cacheValidTime = cacheValidTime;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    public CacheMode getCacheMode() {
        return cacheMode;
    }

    public void setCacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
    }

    public long getCacheValidTime() {
        return cacheValidTime;
    }

    public void setCacheValidTime(long cacheValidTime) {
        this.cacheValidTime = cacheValidTime;
    }
}