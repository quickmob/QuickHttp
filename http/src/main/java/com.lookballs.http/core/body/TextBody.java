package com.lookballs.http.core.body;

import androidx.annotation.NonNull;

import com.lookballs.http.core.ContentType;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * 文本参数提交
 */
public class TextBody extends RequestBody {

    private final String mText;//字符串数据
    private final byte[] mBytes;//字节数组

    public TextBody() {
        this("");
    }

    public TextBody(String text) {
        mText = text;
        mBytes = mText.getBytes();
    }

    @Override
    public MediaType contentType() {
        return ContentType.TEXT;
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
        return mText;
    }

}