/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.jiange.okhttp.volley;


import com.jiange.okhttp.volley.request.BaseResponse;

import cn.ieclipse.af.volley.IBaseResponse;
import cn.ieclipse.af.volley.InterceptorError;
import cn.ieclipse.af.volley.RestError;

/**
 * 类/接口描述
 *
 * @author Harry
 * @date 2016/1/27.
 */
public abstract class AppController<Listener> extends cn.ieclipse.af.volley.Controller<Listener> {

    public AppController() {
        super();
    }

    public AppController(Listener l) {
        super();
        setListener(l);
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
            if ("10000005".equals(error.getStatus()) || "10000004".equals(error.getStatus())) {
                //LoginActivity.go(ADVApplication.getInstance());
                return;
            }
            onError(new RestError(error));
        }
    }
}
