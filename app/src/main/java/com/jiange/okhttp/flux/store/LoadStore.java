/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.jiange.okhttp.flux.store;

import com.jiange.okhttp.flux.flux_lib.action.Action;
import com.jiange.okhttp.flux.flux_lib.store.Store;
import com.jiange.okhttp.volley.request.HouseResponse;

import cn.ieclipse.af.volley.RestError;

/**
 * 类/接口描述
 *
 * @author wangjian
 * @date 2016/3/14.
 */
public class LoadStore extends Store<LoadStore.LoadAction> {

    public HouseResponse getData() {
        return getLoadAction().getData();
    }

    public RestError getError() {
        RestError error = getLoadAction().getError();
        return error;
    }

    public LoadAction getLoadAction(){
        return getAction();
    }

    public static class LoadAction extends Action<HouseResponse ,RestError> {
        public LoadAction() {
        }

        public LoadAction(String type) {
            super(type);
        }

        public LoadAction(String type, HouseResponse data, RestError error) {
            super(type, data, error);
        }
    }
}
