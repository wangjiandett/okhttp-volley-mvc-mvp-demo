/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.jiange.okhttp.mvp.persenter;

import com.jiange.okhttp.mvp.model.AreaModelImp;
import com.jiange.okhttp.mvp.model.AreaModelImp.LoadAreaListener;
import com.jiange.okhttp.mvp.view.AreaView;
import com.jiange.okhttp.volley.request.HouseResponse;

/**
 * 类/接口描述
 *
 * @author wangjian
 * @date 2016/3/16.
 */
public class AreaPersenterImp implements AreaPersenter ,LoadAreaListener{

    AreaModelImp areaModelImp;
    AreaView areaView;

    public AreaPersenterImp(AreaView areaView){
        areaModelImp = new AreaModelImp();
        this.areaView = areaView;
    }

    @Override
    public void loadAreaList() {
        areaModelImp.loadAreaList(this);
    }

    @Override
    public void loadSuccess(HouseResponse response) {
        areaView.loadSuccess(response);
    }

    @Override
    public void loadFail(String msg) {
        areaView.loadFail(msg);
    }
}
