package com.lookballs.app.http.bean;

public class BaseBean<T> {
    public String errorMsg;
    public int errorCode;
    public T data;
}
