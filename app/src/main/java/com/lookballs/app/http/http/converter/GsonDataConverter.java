package com.lookballs.app.http.http.converter;

import androidx.lifecycle.LifecycleOwner;

import com.lookballs.app.http.util.gson.GsonUtil;
import com.lookballs.http.core.converter.IDataConverter;
import com.lookballs.http.utils.QuickLogUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 自定义数据解析
 */
public final class GsonDataConverter implements IDataConverter {

    private static final String TAG = "GsonDataConverter";

    private long getResponseTimeMill(Response response) {
        try {
            //格林威治时间：Thu, 06 Aug 2020 06:45:10 GMT
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);
            Date date = dateFormat.parse(response.header("Date"));
            long gmtTime = date.getTime();
            //转换为当前时区时间
            int offset = Calendar.getInstance().get(Calendar.ZONE_OFFSET);//获取当前时区的偏移时间（单位毫秒）
            long zoneTime = gmtTime + offset;
            return zoneTime;
        } catch (Exception e) {
            return 0;
        }
    }

    private void printText(Response response, String text) {
        StringBuilder sb = new StringBuilder();
        sb.append("请求结果\n");
        sb.append("Url：").append(response.request().url()).append("\n");
        sb.append("ResponseCode：").append(response.code()).append("\n");
        sb.append("ResponseResult：");
        QuickLogUtils.json(QuickLogUtils.logTag, sb.toString(), text);
    }

    @Override
    public Object onSucceed(LifecycleOwner lifecycleOwner, Response response, Type type) throws Exception {
        long currentTime = getResponseTimeMill(response);
        QuickLogUtils.i(TAG, "当前服务器时间：" + currentTime);

        ResponseBody body = response.body();

        String text = body.string();
        //打印文本
        printText(response, text);

        Object result = null;
        if (String.class.equals(type)) {
            //如果这是一个 String 对象
            result = text;
        } else if (JSONObject.class.equals(type)) {
            //如果这是一个 JSONObject 对象
            result = new JSONObject(text);
        } else if (JSONArray.class.equals(type)) {
            //如果这是一个 JSONArray 对象
            result = new JSONArray(text);
        } else {
            //处理Json解析结果
            result = GsonUtil.fromJson(text, type);
        }
        return result;
    }

    @Override
    public Exception onFail(LifecycleOwner lifecycleOwner, Exception e) {
        QuickLogUtils.printStackTrace(e);
        return e;
    }

}