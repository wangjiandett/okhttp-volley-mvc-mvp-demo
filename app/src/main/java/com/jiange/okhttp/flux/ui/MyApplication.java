/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.jiange.okhttp.flux.ui;

import android.app.Application;

import com.jiange.okhttp.okhttp.OKHttpConfig;
import com.jiange.okhttp.okhttp.OKHttpManager;
import com.jiange.okhttp.volley.request.BaseResponse;

import java.io.File;

import cn.ieclipse.af.volley.VolleyConfig;
import cn.ieclipse.af.volley.VolleyManager;
import okhttp3.Cache;

/**
 * 类/接口描述
 *
 * @author wangjian
 * @date 2016/3/14.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        VolleyConfig config = new VolleyConfig.Builder().setBaseResponseClass(BaseResponse.class).build();
        VolleyManager.init(getApplicationContext(), config);

        final File cacheDir = new File(this.getCacheDir(), "HttpResponseCache");

        OKHttpConfig OKHttpConfig = new OKHttpConfig.Builder().setBaseResponseClass(
            com.jiange.okhttp.okhttp.BaseResponse.class).setConnectTimeout(10).setReadTimeout(10).setWriteTimeout(10)
            .setCache(new Cache(cacheDir, 10 * 1024 * 1024)).build();
        OKHttpManager.init(this, OKHttpConfig);

    }
}
