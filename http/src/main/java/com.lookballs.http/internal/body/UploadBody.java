package com.lookballs.http.internal.body;

import com.lookballs.http.core.ContentType;
import com.lookballs.http.core.utils.QuickUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * 上传文件流
 */
public final class UploadBody extends RequestBody {

    private final Source mSource;//上传源
    private final MediaType mMediaType;//内容类型
    private final String mName;//内容名称
    private final long mLength;//内容大小

    public UploadBody(File file) throws FileNotFoundException {
        this(Okio.source(file), QuickUtils.getMimeType(file.getName()), file.getName(), file.length());
    }

    public UploadBody(InputStream inputStream, String name) throws IOException {
        this(Okio.source(inputStream), ContentType.STREAM, name, inputStream.available());
    }

    public UploadBody(Source source, MediaType type, String name, long length) {
        mSource = source;
        mMediaType = type;
        mName = name;
        mLength = length;
    }

    @Override
    public MediaType contentType() {
        return mMediaType;
    }

    @Override
    public long contentLength() {
        return mLength;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        try {
            sink.writeAll(mSource);
        } finally {
            QuickUtils.closeStream(mSource);
        }
    }

    public String getName() {
        return mName;
    }

}