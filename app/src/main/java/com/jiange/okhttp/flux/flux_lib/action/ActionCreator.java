/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.jiange.okhttp.flux.flux_lib.action;


import com.jiange.okhttp.flux.event.LoadFailEvent;
import com.jiange.okhttp.flux.event.LoadStartEvent;
import com.jiange.okhttp.flux.event.LoadSuccessEvent;
import com.jiange.okhttp.flux.event.OtherEvent;
import com.jiange.okhttp.flux.flux_lib.dispatcher.Dispatcher;
import com.jiange.okhttp.volley.LogicError;
import com.jiange.okhttp.volley.request.BaseResponse;

import cn.ieclipse.af.volley.IBaseResponse;
import cn.ieclipse.af.volley.InterceptorError;
import cn.ieclipse.af.volley.RestError;

/**
 * action 生成器
 *
 * @author wangjian
 * @date 2016/1/27.
 */
public abstract class ActionCreator<Listener> extends cn.ieclipse.af.volley.Controller<Listener> {

    protected Dispatcher mDispatcher;

    public ActionCreator() {
        super();
    }

    public ActionCreator(Listener l) {
        super();
        setListener(l);
    }

    public ActionCreator(Dispatcher dispatcher, Listener l) {
        this.mDispatcher = dispatcher;
        setListener(l);
    }

    /**
     * 产生一个type类型的action,可在接收处如下接收处 接收{@link OtherEvent}类型
     *
     * @param type action 类型默认支持<br/>
     *             {@link Action}<br/>
     *             {@link LoadStartEvent}<br/>
     *             {@link LoadFailEvent}<br/>
     *             {@link LoadSuccessEvent}
     */
    public void createAction(String type) {
        mDispatcher.dispatch(new Action(type));
    }

    public void createAction(Action action) {
        mDispatcher.dispatch(action);
    }

    public abstract class AppBaseTask<Input, Output> extends RequestObjectTask<Input, Output> {

        @Override
        public boolean onInterceptor(IBaseResponse response) throws Exception {
            if (response instanceof BaseResponse) {
                BaseResponse resp = (BaseResponse) response;
                if (!"10000001".equals(resp.getStatus())) {
                    onLogicError(new LogicError(null, resp.getStatus(), resp.getMessage()));
                    throw new InterceptorError();
                }
            }
            return false;
        }

        @Override
        public void onError(RestError error) {

        }

        public void onLogicError(LogicError error) {
            if ("105".equals(error.getStatus()) || "104".equals(error.getStatus())) {
                //LoginActivity.go(ADVApplication.getInstance());
                return;
            }
            onError(new RestError(error));
        }
    }
}
