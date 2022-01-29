package com.lookballs.app.http.helper;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebSettings;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class CommonHelper {
    private static String USER_AGENT;

    private static String OKHTTP_USER_AGENT;

    /**
     * 获取okhttp的UserAgent
     * 参考地址https://github.com/liujingxing/okhttp-RxHttp/blob/master/rxhttp/src/main/java/rxhttp/wrapper/OkHttpCompat.java
     */
    public static String getOkHttpUserAgent() {
        if (OKHTTP_USER_AGENT != null) return OKHTTP_USER_AGENT;
        try {
            //4.7.x及以上版本获取userAgent方式
            Class<?> utilClass = Class.forName("okhttp3.internal.Util");
            return OKHTTP_USER_AGENT = (String) utilClass.getDeclaredField("userAgent").get(utilClass);
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
        try {
            Class<?> versionClass = Class.forName("okhttp3.internal.Version");
            try {
                //4.x.x及以上版本获取userAgent方式
                Field userAgent = versionClass.getDeclaredField("userAgent");
                return OKHTTP_USER_AGENT = (String) userAgent.get(versionClass);
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
            try {
                //4.x.x以下版本获取userAgent方式
                Method userAgent = versionClass.getDeclaredMethod("userAgent");
                return OKHTTP_USER_AGENT = (String) userAgent.invoke(versionClass);
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return OKHTTP_USER_AGENT = "okhttp/x.x.x";
    }

    /**
     * 获取UserAgent
     */
    public static String getUserAgent(Context context) {
        if (!TextUtils.isEmpty(USER_AGENT)) {
            return USER_AGENT;
        }
        try {
            String userAgent = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                try {
                    userAgent = WebSettings.getDefaultUserAgent(context);
                } catch (Exception e) {
                    userAgent = System.getProperty("http.agent");
                }
            } else {
                userAgent = System.getProperty("http.agent");
            }
            StringBuffer sb = new StringBuffer();
            //在一些国产手机上面这个User-Agent里面会包含中文，设置到okhttp里面就会报错
            //什么原因引起的呢？okhttp3.Headers$Builder.checkNameAndValue进到这个方法里面可以看到okhttp对中文进行了过滤，如果不符合条件就抛出异常IllegalArgumentException
            //所以对返回结果进行过滤，如果不符合条件，就进行转码。
            for (int i = 0, length = userAgent.length(); i < length; i++) {
                char c = userAgent.charAt(i);
                if (c <= '\u001f' || c >= '\u007f') {
                    sb.append(String.format("\\u%04x", (int) c));
                } else {
                    sb.append(c);
                }
            }
            return USER_AGENT = sb.toString();
        } catch (Exception e) {

        }
        return USER_AGENT = "";
    }
}
