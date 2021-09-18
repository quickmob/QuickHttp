package com.lookballs.http.core.utils;

import android.text.TextUtils;
import android.util.Log;

import com.lookballs.http.QuickHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 日志工具类
 */
public class QuickLogUtils {

    public static final String logTag = "QuickHttp";

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static void printLine(String tag, boolean isTop) {
        if (isTop) {
            Log.w(tag, "╔═══════════════════════════════════════════════════════════════════════════════════════");
        } else {
            Log.w(tag, "╚═══════════════════════════════════════════════════════════════════════════════════════");
        }
    }

    public static void d(String msg) {
        d(logTag, msg);
    }

    public static void d(String tag, String msg) {
        if (QuickHttp.getConfig().isLogEnabled()) {
            Log.d(tag, msg);
        }
    }

    public static void v(String msg) {
        v(logTag, msg);
    }

    public static void v(String tag, String msg) {
        if (QuickHttp.getConfig().isLogEnabled()) {
            Log.v(tag, msg);
        }
    }

    public static void e(String msg) {
        e(logTag, msg);
    }

    public static void e(String tag, String msg) {
        if (QuickHttp.getConfig().isLogEnabled()) {
            Log.e(tag, msg);
        }
    }

    public static void i(String msg) {
        i(logTag, msg);
    }

    public static void i(String tag, String msg) {
        if (QuickHttp.getConfig().isLogEnabled()) {
            Log.i(tag, msg);
        }
    }

    public static void json(String msg) {
        json(logTag, msg);
    }

    public static void json(String tag, String msg) {
        json(tag, "", msg);
    }

    public static void json(String tag, String headMsg, String msg) {
        if (!QuickHttp.getConfig().isLogEnabled()) {
            return;
        }
        String message;
        try {
            if (msg.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(msg);
                message = jsonObject.toString(4);//最重要的方法，就一行，返回格式化的json字符串，其中的数字4是缩进字符数
            } else if (msg.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(msg);
                message = jsonArray.toString(4);
            } else {
                message = msg;
            }
        } catch (JSONException e) {
            message = msg;
        }

        printLine(tag, true);
        if (!TextUtils.isEmpty(headMsg)) {
            message = headMsg + LINE_SEPARATOR + message;
        }
        String[] lines = message.split(LINE_SEPARATOR);
        for (String line : lines) {
            i(tag, "║ " + line);
            System.out.println();
        }
        printLine(tag, false);
    }

    public static void exception(Exception e) {
        exception(logTag, e);
    }

    public static void exception(String tag, Exception e) {
        if (e != null) {
            if (QuickHttp.getConfig().isLogEnabled()) {
                e(tag, e.getClass().getName() + " " + e.getMessage());
            }
        }
    }

    public static void printStackTrace(Throwable t) {
        if (t != null) {
            if (QuickHttp.getConfig().isLogEnabled()) {
                t.printStackTrace();
            }
        }
    }

    public static void printStackTrace(Exception e) {
        if (e != null) {
            if (QuickHttp.getConfig().isLogEnabled()) {
                e.printStackTrace();
            }
        }
    }

}
