/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.jiange.okhttp.okhttp.demo;

import com.jiange.okhttp.okhttp.OKUploadController;
import com.jiange.okhttp.volley.URLConst;

import java.io.File;
import java.util.HashMap;

import cn.ieclipse.af.volley.IUrl;

/**
 * 类/接口描述
 *
 * @author wangjian
 * @date 2016/3/26.
 */
public class UploadController extends OKUploadController<UploadController.UploadListener> {


    public UploadController(UploadListener l) {
        super(l);
    }

    public void upload(File file) {
        UpLoadTask task = new UpLoadTask();
        task.load(file, UploadResponse.class);
    }

    private class UpLoadTask extends BaseUpLoadTask<UploadResponse> {
        @Override
        protected IUrl getUrl() {
            return URLConst.User.post;
        }

        @Override
        protected void onSuccess(UploadResponse baseResponse) {
            mListener.loadSuccess(baseResponse);
        }

        @Override
        protected void onError(String error) {
            mListener.loadFail(error);
        }

        @Override
        protected void addParams(HashMap<String, String> params) {
            params.put("client","Android");
            params.put("uid","1061");
            params.put("token","1911173227afe098143caf4d315a436d");
            params.put("uuid","A000005566DA77");
        }
    }


    public interface UploadListener {
        void loadSuccess(UploadResponse uploadResponse);

        void loadFail(String msg);
    }
}
