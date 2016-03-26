/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.jiange.okhttp.flux.event;

/**
 * 自定义event，必须继承BaseStoreChangeEvent
 *
 * @author wangjian
 * @date 2016/3/15.
 */
public class OtherEvent extends BaseStoreChangeEvent {

    public OtherEvent() {
    }

    public OtherEvent(String type) {
        super(type);
    }
}
