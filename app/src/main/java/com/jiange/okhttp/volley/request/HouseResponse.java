/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.jiange.okhttp.volley.request;

import java.io.Serializable;
import java.util.List;

/**
 * 类/接口描述
 *
 * @author wangjian
 * @date 2016/1/15.
 */
public class HouseResponse implements Serializable {

    public List<Item> regionOption;

    public static class Item {
        public int val;
        public String label;

        @Override
        public boolean equals(Object o) {
            if (o instanceof Item) {
                Item item = (Item) o;
                return item.val == val;
            }
            return super.equals(o);
        }
    }
}
