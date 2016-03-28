/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.jiange.okhttp.okhttp.demo.controller;

import com.jiange.okhttp.okhttp.demo.model.LoadRequest;
import com.jiange.okhttp.okhttp.demo.model.LoadResponse;
import com.jiange.okhttp.okhttp.demo.model.PersonRequest;
import com.jiange.okhttp.okhttp.demo.model.PersonResponse;
import com.jiange.okhttp.volley.URLConst;

import java.util.List;

import cn.ieclipse.af.volley.IUrl;

/**
 * 类/接口描述
 *
 * @author wangjian
 * @date 2016/3/23.
 */
public class LoadController extends MyAppController<LoadController.LoadListener> {

    public LoadController(LoadListener l) {
        mListener = l;
    }

    public void load() {
        Task task = new Task();
        task.load(new LoadRequest(), LoadResponse.class);
    }

    public void post() {
        PostTask task = new PostTask();
        task.load2List(new PersonRequest(), PersonResponse.class, false);
    }

    public class Task extends AppBaseTask<LoadRequest, LoadResponse> {

        @Override
        public IUrl getUrl() {
            return URLConst.User.DATAPIE;
        }

        @Override
        public void onSuccess(LoadResponse loadResponse) {
            mListener.onLoadSuccess(loadResponse);
        }

        @Override
        public void onError(String e) {
            mListener.onLoadFail(e);
        }
    }

    public class PostTask extends AppBaseTask<PersonRequest, List<PersonResponse>> {

        @Override
        public IUrl getUrl() {
            return URLConst.User.PERSONARMORY;
        }

        @Override
        public void onSuccess(List<PersonResponse> responses) {
            mListener.onLoadPersonSuccess(responses);
        }

        @Override
        public void onError(String e) {
            mListener.onLoadFail(e);
        }
    }


    public interface LoadListener {

        void onLoadPersonSuccess(List<PersonResponse> responses);

        void onLoadSuccess(LoadResponse loadResponse);

        void onLoadFail(String e);
    }

}
