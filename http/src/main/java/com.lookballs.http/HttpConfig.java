package com.lookballs.http;

import android.content.Context;

import com.lookballs.http.core.BodyType;
import com.lookballs.http.core.cache.CacheConfig;
import com.lookballs.http.core.cache.CacheMode;
import com.lookballs.http.core.cache.ICacheStrategy;
import com.lookballs.http.core.converter.IDataConverter;
import com.lookballs.http.core.listener.AddHeadersListener;
import com.lookballs.http.core.listener.AddParamsListener;
import com.lookballs.http.core.listener.AddUrlParamsListener;
import com.lookballs.http.core.listener.OnRetryConditionListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * 请求配置类
 */
public class HttpConfig {
    static final OkHttpClient DEFAULT_OK_HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(QuickHttp.DEFAULT_TIMEOUT_TIME, TimeUnit.SECONDS)
            .readTimeout(QuickHttp.DEFAULT_TIMEOUT_TIME, TimeUnit.SECONDS)
            .writeTimeout(QuickHttp.DEFAULT_TIMEOUT_TIME, TimeUnit.SECONDS)
            //.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            //.sslSocketFactory(HttpsUtils.getSslSocketFactory().sSLSocketFactory, HttpsUtils.getSslSocketFactory().trustManager)
            //.hostnameVerifier(HttpsUtils.UnSafeHostnameVerifier)
            //.protocols(Collections.singletonList(Protocol.HTTP_1_1))
            //retryOnConnectionFailure(true);
            //.proxy(Proxy.NO_PROXY)
            //.dns(Dns.SYSTEM)
            .build();

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
    private String baseUrl;
    private CacheConfig cacheConfig;
    private ICacheStrategy cacheStrategy;
    private List<String> excludeCacheKeys = Collections.emptyList();
    private int downloadReadByteSize = QuickHttp.DEFAULT_DOWNLOAD_READ_BYTE_SIZE;

    public HttpConfig() {
        this(new HttpConfig.Builder());
    }

    HttpConfig(HttpConfig.Builder builder) {
        this.context = builder.context;
        this.logEnabled = builder.logEnabled;
        this.bodyType = builder.bodyType;
        this.okHttpClient = builder.okHttpClient;
        this.dataConverter = builder.dataConverter;
        this.addHeadersListener = builder.addHeadersListener;
        this.addParamsListener = builder.addParamsListener;
        this.addUrlParamsListener = builder.addUrlParamsListener;
        this.retryCount = builder.retryCount;
        this.retryDelayMillis = builder.retryDelayMillis;
        this.onRetryConditionListener = builder.onRetryConditionListener;
        this.baseUrl = builder.baseUrl;
        this.cacheConfig = builder.cacheConfig;
        this.cacheStrategy = builder.cacheStrategy;
        this.excludeCacheKeys = builder.excludeCacheKeys;
        this.downloadReadByteSize = builder.downloadReadByteSize;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public IDataConverter getDataConverter() {
        return dataConverter;
    }

    public AddHeadersListener getAddHeadersListener() {
        return addHeadersListener;
    }

    public AddParamsListener getAddParamsListener() {
        return addParamsListener;
    }

    public AddUrlParamsListener getAddUrlParamsListener() {
        return addUrlParamsListener;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public long getRetryDelayMillis() {
        return retryDelayMillis;
    }

    public OnRetryConditionListener getOnRetryConditionListener() {
        return onRetryConditionListener;
    }

    public boolean isLogEnabled() {
        return logEnabled;
    }

    public BodyType getBodyType() {
        return bodyType;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public CacheConfig getCacheConfig() {
        return new CacheConfig(cacheConfig);
    }

    public ICacheStrategy getCacheStrategy() {
        return cacheStrategy;
    }

    public List<String> getExcludeCacheKeys() {
        return excludeCacheKeys;
    }

    public int getDownloadReadByteSize() {
        return downloadReadByteSize;
    }

    public Context getContext() {
        return context;
    }

    public HttpConfig.Builder newBuilder() {
        return new HttpConfig.Builder(this);
    }

    public static final class Builder {
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
        private String baseUrl;
        private CacheConfig cacheConfig;
        private ICacheStrategy cacheStrategy;
        private List<String> excludeCacheKeys = Collections.emptyList();
        private int downloadReadByteSize = QuickHttp.DEFAULT_DOWNLOAD_READ_BYTE_SIZE;

        public Builder() {
            context = null;
            logEnabled = false;
            bodyType = BodyType.FORM;
            okHttpClient = DEFAULT_OK_HTTP_CLIENT;
            dataConverter = null;
            addHeadersListener = null;
            addParamsListener = null;
            addUrlParamsListener = null;
            retryCount = 0;
            retryDelayMillis = 0;
            onRetryConditionListener = null;
            baseUrl = null;
            cacheConfig = null;
            cacheStrategy = null;
            excludeCacheKeys = Collections.emptyList();
            downloadReadByteSize = QuickHttp.DEFAULT_DOWNLOAD_READ_BYTE_SIZE;
        }

        Builder(HttpConfig httpConfig) {
            this.context = httpConfig.context;
            this.logEnabled = httpConfig.logEnabled;
            this.bodyType = httpConfig.bodyType;
            this.okHttpClient = httpConfig.okHttpClient;
            this.dataConverter = httpConfig.dataConverter;
            this.addHeadersListener = httpConfig.addHeadersListener;
            this.addParamsListener = httpConfig.addParamsListener;
            this.addUrlParamsListener = httpConfig.addUrlParamsListener;
            this.retryCount = httpConfig.retryCount;
            this.retryDelayMillis = httpConfig.retryDelayMillis;
            this.onRetryConditionListener = httpConfig.onRetryConditionListener;
            this.baseUrl = httpConfig.baseUrl;
            this.cacheConfig = httpConfig.cacheConfig;
            this.cacheStrategy = httpConfig.cacheStrategy;
            this.excludeCacheKeys = httpConfig.excludeCacheKeys;
            this.downloadReadByteSize = httpConfig.downloadReadByteSize;
        }

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
         * 设置基本url
         *
         * @param baseUrl
         * @return
         */
        public Builder setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        /**
         * 设置缓存
         *
         * @param cacheStrategy 缓存策略
         * @return
         */
        public Builder setCache(ICacheStrategy cacheStrategy) {
            return setCache(CacheMode.ONLY_REQUEST_NETWORK, -1, cacheStrategy);
        }

        /**
         * 设置缓存
         *
         * @param cacheStrategy  缓存策略
         * @param cacheMode      缓存模式
         * @param cacheValidTime 缓存有效时长
         * @return
         */
        public Builder setCache(CacheMode cacheMode, long cacheValidTime, ICacheStrategy cacheStrategy) {
            this.cacheConfig = new CacheConfig(cacheMode, cacheValidTime);
            this.cacheStrategy = cacheStrategy;
            return this;
        }

        /**
         * 过滤要剔除的cacheKey
         *
         * @param keys cacheKey
         * @return
         */
        public Builder setExcludeCacheKeys(String... keys) {
            this.excludeCacheKeys = Arrays.asList(keys);
            return this;
        }

        /**
         * 设置下载时每次读取流的最大值
         *
         * @param downloadReadByteSize 读取流的最大值
         * @return
         */
        public Builder setDownloadReadByteSize(int downloadReadByteSize) {
            this.downloadReadByteSize = downloadReadByteSize;
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
