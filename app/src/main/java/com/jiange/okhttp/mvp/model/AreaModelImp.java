/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.jiange.okhttp.mvp.model;

import com.jiange.okhttp.volley.AppController;
import com.jiange.okhttp.volley.URLConst;
import com.jiange.okhttp.volley.request.HouseRequest;
import com.jiange.okhttp.volley.request.HouseResponse;

import cn.ieclipse.af.volley.RestError;

/**
 * 类/接口描述
 *
 * @author wangjian
 * @date 2016/3/16.
 */
public class AreaModelImp implements AreaModel {

    LoadController controller;

    @Override
    public void loadAreaList(LoadAreaListener loadAreaListener) {
        controller = new LoadController(loadAreaListener);
        controller.load(new HouseRequest());
    }

    private class LoadController extends AppController<LoadAreaListener> {

        public LoadController(LoadAreaListener l) {
            super(l);
        }

        public void load(HouseRequest request) {
            LoadTask task = new LoadTask();
            task.load(request, HouseResponse.class, false);
        }

        private class LoadTask extends LoadController.AppBaseTask<HouseRequest, HouseResponse> {

            @Override
            public URLConst.Url getUrl() {
                return URLConst.User.DATAPIE;
            }

            @Override
            public void onSuccess(HouseResponse out, boolean fromCache) {
                mListener.loadSuccess(out);
            }

            @Override
            public void onError(RestError error) {
                mListener.loadFail(error.getMessage());
            }
        }
    }


    public interface LoadAreaListener {
        void loadSuccess(HouseResponse response);

        void loadFail(String msg);
    }
}
