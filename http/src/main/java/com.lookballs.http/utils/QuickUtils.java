package com.lookballs.http.utils;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.lookballs.http.core.model.ContentType;
import com.lookballs.http.core.model.HttpHeaders;
import com.lookballs.http.core.model.HttpUrlParams;
import com.lookballs.http.internal.GsonPreconditions;
import com.lookballs.http.internal.GsonTypes;
import com.lookballs.http.internal.body.UploadBody;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 工具类
 */
public class QuickUtils {

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    /**
     * 只有满足条件才在主线程中执行
     */
    public static boolean runOnUiThread(boolean execute, Runnable r) {
        if (execute) {
            return runOnUiThread(r);
        }
        return false;
    }

    /**
     * 在主线程中执行
     */
    public static boolean runOnUiThread(Runnable r) {
        return HANDLER.post(r);
    }

    /**
     * 只有满足条件才在延迟一段时间执行
     */
    public static boolean postDelayed(boolean execute, Runnable r, long delayMillis) {
        if (execute) {
            return postDelayed(r, delayMillis);
        }
        return false;
    }

    /**
     * 延迟一段时间执行
     */
    public static boolean postDelayed(Runnable r, long delayMillis) {
        return HANDLER.postDelayed(r, delayMillis);
    }

    /**
     * 获取校验后的context
     */
    public static Context getCheckContext(Object mContext) {
        if (mContext instanceof Fragment) {
            return ((Fragment) mContext).getActivity();
        } else if (mContext instanceof android.app.Fragment) {
            return ((android.app.Fragment) mContext).getActivity();
        } else {
            if (mContext instanceof Activity) {
                return (Activity) mContext;
            } else if (mContext instanceof ContextWrapper) {
                return ((ContextWrapper) mContext).getBaseContext();
            } else if (mContext instanceof Application) {
                return ((Application) mContext).getApplicationContext();
            } else {
                return null;
            }
        }
    }

    /**
     * 获取泛型类型
     */
    public static <B> Type getParameterizedType(Class<B> clazz) {
        return GsonTypes.canonicalize(GsonPreconditions.checkNotNull(clazz));
    }

    /**
     * 获取泛型类型
     */
    public static Type getParameterizedType(Object object) {
        return getParameterizedType(object, 0);
    }

    /**
     * 获取泛型类型
     */
    public static Type getParameterizedType(Object object, int index) {
        Type tempType;
        Type[] types = object.getClass().getGenericInterfaces();
        if (types != null && types.length > 0) {
            //如果这个监听对象是直接实现了接口
            tempType = types[index];
        } else {
            //如果这个监听对象是通过类继承
            tempType = object.getClass().getGenericSuperclass();
        }
        if (!(tempType instanceof ParameterizedType)) {
            throw new IllegalStateException(">>>Missing type parameter");
        }
        Type type = ((ParameterizedType) tempType).getActualTypeArguments()[index];
        return type;
    }

    /**
     * 获取进度百分比
     */
    public static int getProgress(long totalByte, long currentByte) {
        //计算百分比，这里踩了两个坑
        //当文件很大的时候：字节数 * 100 会超过 int 最大值，计算结果会变成负数
        //还有需要注意的是，long 除以 long 等于 long，这里的字节数除以总字节数应该要 double 类型的
        return (int) (((double) currentByte / totalByte) * 100);
    }

    /**
     * 获取精确进度百分比
     */
    public static double getPreciseProgress(long totalByte, long currentByte) {
        return Double.parseDouble(div(String.valueOf(totalByte), String.valueOf(currentByte), 2, false));
    }

    /**
     * 提供精确的除法运算
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 保留scale位小数
     * @param isIn  是否四舍五入
     * @return
     */
    public static String div(String v1, String v2, int scale, boolean isIn) {
        if (scale < 0) {
            throw new IllegalArgumentException("保留的小数位数必须大于零");
        }
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.divide(b2, scale, isIn ? BigDecimal.ROUND_HALF_UP : BigDecimal.ROUND_FLOOR).toString();
    }

    /**
     * 根据文件名获取 MIME 类型
     */
    public static MediaType getMimeType(String fileName) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        //解决文件名中含有#号异常的问题
        fileName = fileName.replace("#", "");
        String contentType = fileNameMap.getContentTypeFor(fileName);
        if (contentType == null) {
            return ContentType.STREAM;
        }
        MediaType type = MediaType.parse(contentType);
        if (type == null) {
            type = ContentType.STREAM;
        }
        return type;
    }

    /**
     * 获取拼接后的url
     */
    public static String getSplitUrl(String url, HttpUrlParams httpUrlParams) {
        StringBuilder sb = new StringBuilder();
        if (!httpUrlParams.isEmpty() && httpUrlParams.getParams().size() > 0) {
            if (url.indexOf('&') > 0 || url.indexOf('?') > 0) {
                sb.append("&");
            } else {
                sb.append("?");
            }
            for (String key : httpUrlParams.getParams().keySet()) {
                sb.append(key).append("=").append(httpUrlParams.getParams().get(key)).append("&");
            }
        }
        //去除最后一个字符
        String params = sb.toString();
        if (params.length() > 0) {
            params = params.substring(0, params.length() - 1);
        }
        return url + params;
    }

    /**
     * 添加请求头
     */
    public static void addHttpHeaders(Request.Builder request, HttpHeaders headers) {
        if (!headers.isEmpty()) {
            for (String key : headers.getKeys()) {
                request.addHeader(key, headers.getValue(key));
            }
        }
    }

    /**
     * 判断一下这个集合装载的类型是不是 File
     */
    public static boolean isFileList(List list) {
        if (list != null && !list.isEmpty()) {
            for (Object object : list) {
                if (!(object instanceof File)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 字符串编码
     */
    public static String encodeString(String text) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }
        return URLEncoder.encode(text);
    }

    /**
     * 根据 File 对象创建一个流媒体
     */
    public static MultipartBody.Part createPart(String key, File file) {
        try {
            //文件名必须不能带中文，所以这里要编码
            return MultipartBody.Part.createFormData(key, encodeString(file.getName()), new UploadBody(file));
        } catch (Exception e) {
            QuickLogUtils.exception(e);
        }
        return null;
    }

    /**
     * 根据 InputStream 对象创建一个流媒体
     */
    public static MultipartBody.Part createPart(String key, InputStream inputStream) {
        try {
            return MultipartBody.Part.createFormData(key, null, new UploadBody(inputStream, key));
        } catch (Exception e) {
            QuickLogUtils.exception(e);
        }
        return null;
    }

    /**
     * 创建Request.Builder
     */
    public static Request.Builder createRequestBuilder(String url, Object tag, HttpHeaders headers) {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        if (tag != null) {
            requestBuilder.tag(tag);
        }
        addHttpHeaders(requestBuilder, headers);
        return requestBuilder;
    }

    /**
     * 判断是否包含存在流参数
     */
    public static boolean isMultipart(Object object) {
        if (object instanceof File) {//如果这是一个文件
            return true;
        } else if (object instanceof InputStream) {//如果这是一个输入流
            return true;
        } else if (object instanceof MultipartBody.Part) {//如果这是一个自定义的MultipartBody.Part对象
            return true;
        } else if (object instanceof RequestBody) {//如果这是一个自定义RequestBody
            return true;
        }
        return false;
    }

    /**
     * 关闭流
     */
    public static void closeStream(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception e) {
            QuickLogUtils.exception(e);
        }
    }

    /**
     * 获取文件的 MD5
     */
    public static String getFileMD5(File file) {
        if (file == null) {
            return null;
        }
        DigestInputStream dis = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            MessageDigest md = MessageDigest.getInstance("MD5");
            dis = new DigestInputStream(fis, md);
            byte[] buffer = new byte[1024 * 256];
            while (true) {
                if (!(dis.read(buffer) > 0)) {
                    break;
                }
            }
            md = dis.getMessageDigest();
            byte[] md5 = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : md5) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString().toLowerCase();
        } catch (Exception e) {
            QuickLogUtils.exception(e);
        } finally {
            closeStream(dis);
        }
        return null;
    }
}
