package com.lookballs.app.http.helper;

import android.content.Context;
import android.net.TrafficStats;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.webkit.WebSettings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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

    public static long lastTotalRxBytes;
    public static long lastTimeStamp;

    /**
     * 获取网络下载速度
     *
     * @param uid
     * @return
     */
    public static long getNetSpeed(int uid, long second) {
        long nowTotalRxBytes = getTotalRxBytes(uid);
        long nowTimeStamp = SystemClock.elapsedRealtime();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * second / (nowTimeStamp - lastTimeStamp));

        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        return speed;
    }

    public static long getTotalRxBytes(int uid) {
        try {
            long uidRxBytes1 = TrafficStats.getUidRxBytes(uid);
            long uidRxBytes2 = getUidStat(uid)[1];
            return uidRxBytes1 == TrafficStats.UNSUPPORTED ? uidRxBytes2 : uidRxBytes1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static long[] getUidStat(int uid) {
        String line, line2;
        long[] stats = new long[2];
        try {
            //计算发送和接收的字节数
            File fileSnd = new File("/proc/uid_stat/" + uid + "/tcp_snd");
            File fileRcv = new File("/proc/uid_stat/" + uid + "/tcp_rcv");
            BufferedReader br1 = new BufferedReader(new FileReader(fileSnd));
            BufferedReader br2 = new BufferedReader(new FileReader(fileRcv));
            while ((line = br1.readLine()) != null && (line2 = br2.readLine()) != null) {
                stats[0] = Long.parseLong(line);
                stats[1] = Long.parseLong(line2);
            }
            br1.close();
            br2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }

    /**
     * 下载速度字符串格式化
     *
     * @param speed
     * @return
     */
    public static String getSpeedFormat(long speed) {
        String result;
        if (speed > (1024f * 1024f)) {
            float mbs = speed / 1024f / 1024f;
            result = String.format("%.2f", mbs) + "MB/S";
        } else {
            float kbs = speed / 1024f;
            result = String.format("%.2f", kbs) + "KB/S";
        }
        return result;
    }
}
