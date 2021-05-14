package com.lookballs.app.http.util.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.reflect.TypeToken;
import com.lookballs.app.http.util.gson.adapter.BooleanTypeAdapter;
import com.lookballs.app.http.util.gson.adapter.DoubleTypeAdapter;
import com.lookballs.app.http.util.gson.adapter.FloatTypeAdapter;
import com.lookballs.app.http.util.gson.adapter.IntegerTypeAdapter;
import com.lookballs.app.http.util.gson.adapter.ListTypeAdapter;
import com.lookballs.app.http.util.gson.adapter.LongTypeAdapter;
import com.lookballs.app.http.util.gson.adapter.StringTypeAdapter;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * gson解析工具类
 */
public class GsonUtil {

    private static Gson mGson = null;

    private GsonUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 获取GsonBuilder对象
     */
    private static GsonBuilder gsonBuilder() {
        GsonBuilder gsonBulder = new GsonBuilder();
        gsonBulder
                .registerTypeAdapterFactory(TypeAdapters.newFactory(String.class, new StringTypeAdapter()))
                .registerTypeAdapterFactory(TypeAdapters.newFactory(boolean.class, Boolean.class, new BooleanTypeAdapter()))
                .registerTypeAdapterFactory(TypeAdapters.newFactory(int.class, Integer.class, new IntegerTypeAdapter()))
                .registerTypeAdapterFactory(TypeAdapters.newFactory(long.class, Long.class, new LongTypeAdapter()))
                .registerTypeAdapterFactory(TypeAdapters.newFactory(float.class, Float.class, new FloatTypeAdapter()))
                .registerTypeAdapterFactory(TypeAdapters.newFactory(double.class, Double.class, new DoubleTypeAdapter()))
                .registerTypeHierarchyAdapter(List.class, new ListTypeAdapter());
        gsonBulder.serializeNulls().disableHtmlEscaping();
        return gsonBulder;
    }

    /**
     * 获取gson对象
     */
    public static Gson getGson() {
        if (mGson == null) {
            mGson = gsonBuilder().create();
        }
        return mGson;
    }

    /**
     * object序列化为json
     */
    public static String toJson(final Object object) {
        return toJson(getGson(), object);
    }

    public static String toJson(final Gson gson, final Object object) {
        return gson.toJson(object);
    }

    public static String toJson(final Object src, final Type typeOfSrc) {
        return toJson(getGson(), src, typeOfSrc);
    }

    public static String toJson(final Gson gson, final Object src, final Type typeOfSrc) {
        return gson.toJson(src, typeOfSrc);
    }

    /**
     * json转换为给定类型
     */
    public static <T> T fromJson(final String json, final Class<T> classOfT) {
        return fromJson(getGson(), json, classOfT);
    }

    public static <T> T fromJson(final String json, final Type typeOfT) {
        return fromJson(getGson(), json, typeOfT);
    }

    public static <T> T fromJson(final Reader reader, final Class<T> classOfT) {
        return fromJson(getGson(), reader, classOfT);
    }

    public static <T> T fromJson(final Reader reader, final Type typeOfT) {
        return fromJson(getGson(), reader, typeOfT);
    }

    public static <T> T fromJson(final Gson gson, final String json, final Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    public static <T> T fromJson(final Gson gson, final String json, final Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }

    public static <T> T fromJson(final Gson gson, final Reader reader, final Class<T> classOfT) {
        return gson.fromJson(reader, classOfT);
    }

    public static <T> T fromJson(final Gson gson, final Reader reader, final Type typeOfT) {
        return gson.fromJson(reader, typeOfT);
    }

    public static <T> List<T> fromJson(final String json, final TypeToken typeToken) {
        return fromJson(json, typeToken.getType());
    }

    /**
     * json转换为List<T>
     */
    public static <T> List<T> getList(final String json) {
        List<T> value = fromJson(json, new TypeToken<List<T>>() {
        }.getType());
        return value;
    }

    /**
     * json转换为Set<T>
     */
    public static <T> Set<T> getSet(final String json) {
        Set<T> value = fromJson(json, new TypeToken<Set<T>>() {
        }.getType());
        return value;
    }

    /**
     * json转换为Map<K, V>
     */
    public static <K, V> Map<K, V> getMap(final String json) {
        Map<K, V> value = fromJson(json, new TypeToken<Map<K, V>>() {
        }.getType());
        return value;
    }

    /**
     * 获取type
     */
    public static Type getListType(final Type type) {
        return TypeToken.getParameterized(List.class, type).getType();
    }

    public static Type getSetType(final Type type) {
        return TypeToken.getParameterized(Set.class, type).getType();
    }

    public static Type getMapType(final Type keyType, final Type valueType) {
        return TypeToken.getParameterized(Map.class, keyType, valueType).getType();
    }

    public static Type getArrayType(final Type type) {
        return TypeToken.getArray(type).getType();
    }

    public static Type getType(final Type rawType, final Type... typeArguments) {
        return TypeToken.getParameterized(rawType, typeArguments).getType();
    }

}
