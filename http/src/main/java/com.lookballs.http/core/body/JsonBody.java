package com.lookballs.http.core.body;

import androidx.annotation.NonNull;

import com.lookballs.http.core.ContentType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * Json参数提交
 */
public class JsonBody extends RequestBody {

    private final String mJson;//Json数据
    private final byte[] mBytes;//字节数组

    public JsonBody(Map map) {
        this(new JSONObject(map));
    }

    public JsonBody(JSONObject jsonObject) {
        mJson = jsonObject.toString();
        mBytes = mJson.getBytes();
    }

    public JsonBody(List list) {
        this(new JSONArray(list));
    }

    public JsonBody(JSONArray jsonArray) {
        mJson = jsonArray.toString();
        mBytes = mJson.getBytes();
    }

    public JsonBody(String json) {
        mJson = json;
        mBytes = mJson.getBytes();
    }

    @Override
    public MediaType contentType() {
        return ContentType.JSON;
    }

    @Override
    public long contentLength() {
        //需要注意：这里需要用字节数组的长度来计算
        return mBytes.length;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        sink.write(mBytes, 0, mBytes.length);
    }

    @NonNull
    @Override
    public String toString() {
        return mJson;
    }

}