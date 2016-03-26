package com.jiange.okhttp.flux.flux_lib.action;


/**
 * 传递的action基类
 *
 * @author wangjian
 * @date 2016/3/14.
 */
public class Action<T, E> {

    /**
     * 加载开始
     */
    public static final String ACTION_TYPE_LOADING_START = "loading_start";
    /**
     * 加载失败
     */
    public static final String ACTION_TYPE_LOADING_FAIL = "loading_fail";
    /**
     * 加载成功
     */
    public static final String ACTION_TYPE_LOADING_SUCCESS = "loading_success";

    // 数据类型
    private String type;
    // 数据
    private T data;
    // 异常、错误类型
    private E error;

    public Action() {

    }

    public Action(String type) {
        this.type = type;
    }

    public Action(String type, T data) {
        this.type = type;
        this.data = data;
    }

    public Action(E error, String type) {
        this.type = type;
        this.error = error;
    }

    public Action(String type, T data, E error) {
        this.type = type;
        this.data = data;
        this.error = error;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public E getError() {
        return error;
    }

    public void setError(E error) {
        this.error = error;
    }
}
