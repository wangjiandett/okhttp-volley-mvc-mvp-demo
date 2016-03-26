/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.jiange.okhttp.mvp.model;

/**
 * 类/接口描述
 *
 * @author wangjian
 * @date 2016/3/16.
 */
public interface AreaModel {

    void loadAreaList(AreaModelImp.LoadAreaListener loadAreaListener);
}
