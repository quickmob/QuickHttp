package com.lookballs.app.http.http.interceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okio.AsyncTimeout;

/**
 * 自定义重试超时时间
 */
public class CustomRetryIntercepter implements Interceptor {
    private int maxRetryCount;
    private int currentRetryCount = 0;
    private Call call;
    private AsyncTimeout timeout;

    public CustomRetryIntercepter(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
        timeout = new AsyncTimeout() {
            @Override
            protected void timedOut() {
                if (call != null) {
                    call.cancel();
                }
            }
        };
        timeout.timeout(5 * maxRetryCount, TimeUnit.SECONDS);
    }

    @Override
    public Response intercept(Chain chain) {
        return retry(chain);
    }

    private Response retry(Chain chain) {
        timeout.enter();
        Response response = null;
        call = chain.call();
        Request request = chain.request();
        try {
            response = chain.proceed(request);
            while (!response.isSuccessful() && currentRetryCount < maxRetryCount) {
                currentRetryCount++;
                response = retry(chain);
            }
            timeout.exit();
        } catch (Exception e) {
            while (currentRetryCount < maxRetryCount) {
                currentRetryCount++;
                response = retry(chain);
            }
            timeout.exit();
        }
        return response;
    }
}