/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.jiange.okhttp.okhttp.demo.controller;

import android.os.Environment;

import com.jiange.okhttp.okhttp.OKDownLoadController;
import com.jiange.okhttp.okhttp.demo.model.DownRequest;
import com.jiange.okhttp.volley.URLConst;

import java.io.File;

import cn.ieclipse.af.volley.IUrl;

/**
 * 类/接口描述
 *
 * @author wangjian
 * @date 2016/3/28.
 */
public class OKDownController extends OKDownLoadController<OKDownController.DownLoadListener> {

    public OKDownController(DownLoadListener l) {
        super(l);
    }

    public void downLoad(){
        DownTask downTask = new DownTask();
        downTask.load(new DownRequest(), File.class);
    }

    private class DownTask extends BaseDownLoadTask<DownRequest>{
        @Override
        protected void inProgress(float progress, long total) {
            mListener.inDownLoaProgress(progress, total);
        }

        @Override
        protected String getFileDir() {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }

        @Override
        protected String getFileName() {
            return System.currentTimeMillis()+".apk";
        }

        @Override
        protected IUrl getUrl() {
            return URLConst.User.download;
        }

        @Override
        protected void onSuccess(File file) {
            mListener.onDownLoaSuccess(file);
        }

        @Override
        protected void onError(String error) {

        }
    }

    public interface DownLoadListener{
        void inDownLoaProgress(float progress, long total);
        void onDownLoaSuccess(File file);
    }


}
