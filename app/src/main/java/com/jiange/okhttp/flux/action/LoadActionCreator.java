/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.jiange.okhttp.flux.action;

import com.jiange.okhttp.flux.flux_lib.action.Action;
import com.jiange.okhttp.flux.flux_lib.action.ActionCreator;
import com.jiange.okhttp.flux.flux_lib.dispatcher.Dispatcher;
import com.jiange.okhttp.flux.store.LoadStore;
import com.jiange.okhttp.volley.URLConst;
import com.jiange.okhttp.volley.request.HouseRequest;
import com.jiange.okhttp.volley.request.HouseResponse;

import cn.ieclipse.af.volley.RestError;

/**
 * 类/接口描述
 *
 * @author wangjian
 * @date 2016/3/14.
 */
public class LoadActionCreator extends ActionCreator<LoadActionCreator.LoginListener> {


    public LoadActionCreator(Dispatcher dispatcher, LoginListener l) {
        super(dispatcher, l);
    }

    public void load(HouseRequest request) {
        LoadTask task = new LoadTask();
        task.load(request, HouseResponse.class, false);
    }

    private class LoadTask extends AppBaseTask<HouseRequest, HouseResponse> {

        @Override
        public URLConst.Url getUrl() {
            createAction(new LoadStore.LoadAction(Action.ACTION_TYPE_LOADING_START, null, null));
            return URLConst.User.DATAPIE;
        }

        @Override
        public void onSuccess(HouseResponse out, boolean fromCache) {
            createAction(new LoadStore.LoadAction(Action.ACTION_TYPE_LOADING_SUCCESS, out, null));
        }

        @Override
        public void onError(RestError error) {
            createAction(new LoadStore.LoadAction(Action.ACTION_TYPE_LOADING_FAIL, null, error));
        }
    }

    /**
     * 登录回调
     */
    public interface LoginListener {
        void onLoginCodeSuccess(HouseResponse ou);

        void onLoginCodeFail(RestError error);
    }
}
