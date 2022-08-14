package com.lookballs.http.internal.interceptor;

import com.lookballs.http.core.cache.CacheConfig;
import com.lookballs.http.core.cache.CacheMode;
import com.lookballs.http.core.cache.ICacheStrategy;
import com.lookballs.http.core.exception.CacheReadFailedException;
import com.lookballs.http.core.utils.QuickLogUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 自定义缓存拦截器
 */
public class CacheInterceptor implements Interceptor {
    private CacheConfig cacheConfig;
    private ICacheStrategy cacheStrategy;

    public CacheInterceptor(CacheConfig cacheConfig, ICacheStrategy cacheStrategy) {
        this.cacheConfig = cacheConfig;
        this.cacheStrategy = cacheStrategy;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        //先读取缓存
        Response cacheResponse = readCacheDirect(request);
        if (cacheResponse != null) {
            return cacheResponse;
        }
        try {
            //发起请求
            Response response = chain.proceed(request);
            if (!checkCacheMode(CacheMode.ONLY_REQUEST_NETWORK)) {
                //非ONLY_NETWORK模式下，请求成功，写入缓存
                cacheStrategy.put(response, cacheConfig.getCacheKey());
            }
            return response;
        } catch (IOException e) {
            Response networkResponse = null;
            if (checkCacheMode(CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE)) {
                //请求失败，读取缓存
                networkResponse = getCacheResponse(request);
            }
            if (networkResponse != null) {
                return networkResponse;
            } else {
                throw e;
            }
        }
    }

    //判断直接读取缓存
    private Response readCacheDirect(Request request) throws IOException {
        if (checkCacheMode(CacheMode.ONLY_READ_CACHE, CacheMode.READ_CACHE_FAILED_REQUEST_NETWORK)) {
            Response cacheResponse = getCacheResponse(request);
            if (cacheResponse != null) {
                return cacheResponse;
            } else {
                //ONLY_READ_CACHE模式下，若未读取到缓存则直接抛出异常
                if (checkCacheMode(CacheMode.ONLY_READ_CACHE)) {
                    throw new CacheReadFailedException("cache read failed");
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    //判断缓存模式
    private boolean checkCacheMode(CacheMode... cacheModes) {
        CacheMode cacheMode = cacheConfig.getCacheMode();
        for (CacheMode mode : cacheModes) {
            if (mode == cacheMode) {
                return true;
            }
        }
        return false;
    }

    //读取缓存
    private Response getCacheResponse(Request request) {
        try {
            Response cacheResponse = cacheStrategy.get(request, cacheConfig.getCacheKey());
            if (cacheResponse != null) {
                long receivedTime = cacheResponse.receivedResponseAtMillis();
                long cacheValidTime = cacheConfig.getCacheValidTime();
                if (cacheValidTime != -1L && System.currentTimeMillis() - receivedTime > cacheValidTime) {
                    return null;//缓存过期，返回null
                } else {
                    return cacheResponse;
                }
            }
        } catch (Exception e) {
            QuickLogUtils.printStackTrace(e);
        }
        return null;
    }
}
