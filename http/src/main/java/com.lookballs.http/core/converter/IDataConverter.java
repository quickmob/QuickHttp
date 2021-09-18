package com.lookballs.http.core.converter;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import java.lang.reflect.Type;

import okhttp3.Request;
import okhttp3.Response;

/**
 * 数据转换器
 */
public interface IDataConverter {
    /**
     * 请求开始时回调
     *
     * @param lifecycleOwner 有生命周期的对象
     * @param url            请求接口路径
     * @param request        请求对象
     * @return
     */
    default Request onStart(@Nullable LifecycleOwner lifecycleOwner, String url, Request request) {
        return request;
    }

    /**
     * 请求成功时回调
     *
     * @param lifecycleOwner 有生命周期的对象
     * @param url            请求接口路径
     * @param response       响应对象
     * @param type           解析类型
     * @return
     */
    Object onSucceed(@Nullable LifecycleOwner lifecycleOwner, String url, Response response, Type type) throws Exception;

    /**
     * 请求失败时回调
     *
     * @param lifecycleOwner 有生命周期的对象
     * @param url            请求接口路径
     * @param e              错误对象
     * @return
     */
    Exception onFail(@Nullable LifecycleOwner lifecycleOwner, String url, Exception e);
}
