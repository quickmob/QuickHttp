package com.lookballs.http.config;

import android.content.Context;

import com.lookballs.http.core.converter.IDataConverter;
import com.lookballs.http.core.model.BodyType;
import com.lookballs.http.listener.AddHeadersListener;
import com.lookballs.http.listener.AddParamsListener;
import com.lookballs.http.listener.AddUrlParamsListener;
import com.lookballs.http.listener.OnRetryConditionListener;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * 请求配置类
 */
public class HttpConfig {

    private Builder builder;

    private HttpConfig(Builder builder) {
        this.builder = builder;
        if (this.builder.okHttpClient == null) {
            this.builder.okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    //.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                    //.sslSocketFactory(HttpsUtils.getSslSocketFactory().sSLSocketFactory, HttpsUtils.getSslSocketFactory().trustManager)
                    //.hostnameVerifier(HttpsUtils.UnSafeHostnameVerifier)
                    //.protocols(Collections.singletonList(Protocol.HTTP_1_1))
                    //retryOnConnectionFailure(true);
                    //.proxy(Proxy.NO_PROXY)
                    //.dns(Dns.SYSTEM)
                    .build();
        }
    }

    public OkHttpClient getOkHttpClient() {
        return builder.okHttpClient;
    }

    public IDataConverter getDataConverter() {
        return builder.dataConverter;
    }

    public AddHeadersListener getAddHeadersListener() {
        return builder.addHeadersListener;
    }

    public AddParamsListener getAddParamsListener() {
        return builder.addParamsListener;
    }

    public AddUrlParamsListener getAddUrlParamsListener() {
        return builder.addUrlParamsListener;
    }

    public int getRetryCount() {
        return builder.retryCount;
    }

    public long getRetryDelayMillis() {
        return builder.retryDelayMillis;
    }

    public OnRetryConditionListener getOnRetryConditionListener() {
        return builder.onRetryConditionListener;
    }

    public boolean isLogEnabled() {
        return builder.logEnabled;
    }

    public BodyType getBodyType() {
        return builder.bodyType;
    }

    public Context getContext() {
        return builder.context;
    }

    public static class Builder {
        private Context context;
        private boolean logEnabled = false;
        private BodyType bodyType = BodyType.FORM;
        private OkHttpClient okHttpClient;
        private IDataConverter dataConverter;
        private AddHeadersListener addHeadersListener;
        private AddParamsListener addParamsListener;
        private AddUrlParamsListener addUrlParamsListener;
        private int retryCount = 0;
        private long retryDelayMillis = 0;
        private OnRetryConditionListener onRetryConditionListener;

        /**
         * 设置OkHttpClient
         *
         * @param okHttpClient
         * @return
         */
        public Builder setHttpClient(OkHttpClient okHttpClient) {
            this.okHttpClient = okHttpClient;
            return this;
        }

        /**
         * 设置数据处理器
         *
         * @param dataConverter
         * @return
         */
        public Builder setDataConverter(IDataConverter dataConverter) {
            this.dataConverter = dataConverter;
            return this;
        }

        /**
         * 添加公共请求头监听
         *
         * @param listener
         * @return
         */
        public Builder setAddHeadersListener(AddHeadersListener listener) {
            this.addHeadersListener = listener;
            return this;
        }

        /**
         * 添加公共参数监听
         *
         * @param listener
         * @return
         */
        public Builder setAddParamsListener(AddParamsListener listener) {
            this.addParamsListener = listener;
            return this;
        }

        /**
         * 添加url公共参数拼接监听
         *
         * @param listener
         * @return
         */
        public Builder setAddUrlParamsListener(AddUrlParamsListener listener) {
            this.addUrlParamsListener = listener;
            return this;
        }

        /**
         * 设置重试
         *
         * @param retryCount 重试次数
         * @return
         */
        public Builder setRetry(int retryCount) {
            return setRetry(retryCount, 0, null);
        }

        /**
         * 设置重试
         *
         * @param retryCount       重试次数
         * @param retryDelayMillis 重试延迟时间（单位毫秒）
         * @return
         */
        public Builder setRetry(int retryCount, long retryDelayMillis) {
            return setRetry(retryCount, retryDelayMillis, null);
        }

        /**
         * 设置重试
         *
         * @param retryCount 重试次数
         * @param listener   重试条件回调
         * @return
         */
        public Builder setRetry(int retryCount, OnRetryConditionListener listener) {
            return setRetry(retryCount, 0, listener);
        }

        /**
         * 设置重试
         *
         * @param retryCount       重试次数
         * @param retryDelayMillis 重试延迟时间（单位毫秒）
         * @param listener         重试条件回调
         * @return
         */
        public Builder setRetry(int retryCount, long retryDelayMillis, OnRetryConditionListener listener) {
            if (retryCount < 0) {
                retryCount = 0;
            }
            if (retryDelayMillis < 0) {
                retryDelayMillis = 0;
            }
            this.retryCount = retryCount;
            this.retryDelayMillis = retryDelayMillis;
            this.onRetryConditionListener = listener;
            return this;
        }

        /**
         * 设置日志开关
         *
         * @param logEnabled
         * @return
         */
        public Builder setLogEnabled(boolean logEnabled) {
            this.logEnabled = logEnabled;
            return this;
        }

        /**
         * 设置请求提交方式
         *
         * @param bodyType
         * @return
         */
        public Builder setBodyType(BodyType bodyType) {
            this.bodyType = bodyType;
            return this;
        }

        /**
         * 创建HttpConfig
         *
         * @param context
         * @return
         */
        public HttpConfig build(Context context) {
            this.context = context;
            return new HttpConfig(this);
        }
    }

}
