package com.lookballs.app.http;

import android.app.Application;
import android.content.Context;

import com.lookballs.app.http.http.CustomOkHttpClient;
import com.lookballs.app.http.http.converter.DataConverter;
import com.lookballs.http.QuickHttp;
import com.lookballs.http.HttpConfig;
import com.lookballs.http.core.listener.AddHeadersListener;
import com.lookballs.http.core.listener.OnRetryConditionListener;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Dns;

public class StartApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initOkhttpConfig(this);
    }

    /**
     * 初始化Okhttp网络请求配置20210513
     */
    private void initOkhttpConfig(Context mContext) {
        CustomOkHttpClient.getInstance().createOkHttpClient(15000, Dns.SYSTEM);

        HttpConfig.Builder builder = new HttpConfig.Builder();
        builder.setLogEnabled(true);
        builder.setHttpClient(CustomOkHttpClient.getInstance().getOkHttpClient());
        builder.setDataConverter(new DataConverter());
        builder.setBaseUrl("https://www.wanandroid.com/");
        builder.setRetry(2, 1000, new OnRetryConditionListener() {
            @Override
            public boolean retryCondition(Exception e) {
                return e instanceof SocketTimeoutException || e instanceof UnknownHostException;
            }
        });
        builder.setAddHeadersListener(new AddHeadersListener() {
            @Override
            public Map<String, String> applyMap() {
                Map<String, String> map = new HashMap<>();
                map.put("token", "39f18f20189b0a35cf56681c9bf53ba77");
                return map;
            }
        });
        QuickHttp.init(builder.build(mContext));
    }

}
