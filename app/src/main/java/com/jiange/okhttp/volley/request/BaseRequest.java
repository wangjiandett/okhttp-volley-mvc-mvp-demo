/**
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.jiange.okhttp.volley.request;

import java.io.Serializable;

/**
 * 所有model的父类
 */
public class BaseRequest implements Serializable {
    public int page;
    public int city_id = 2;
    public String client = "Android";
    public String uuid = "A000005566DA77";
}
