/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.jiange.okhttp.mvp.view;

import com.jiange.okhttp.volley.request.HouseResponse;

/**
 * 类/接口描述
 *
 * @author wangjian
 * @date 2016/3/16.
 */
public interface AreaView {
    void loadSuccess(HouseResponse response);
    void loadFail(String msg);
    void showProgress();
}
