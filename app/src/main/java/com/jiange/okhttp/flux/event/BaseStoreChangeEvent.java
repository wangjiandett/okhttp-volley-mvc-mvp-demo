/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.jiange.okhttp.flux.event;

/**
 * 所有store更新事件的父类
 *
 * @author wangjian
 * @date 2016/3/14.
 */
public class BaseStoreChangeEvent {

    public String type;

    public BaseStoreChangeEvent() {
    }

    public BaseStoreChangeEvent(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
