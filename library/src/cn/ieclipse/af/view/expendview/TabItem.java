/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package cn.ieclipse.af.view.expendview;

import java.io.Serializable;

/**
 * 类/接口描述
 *
 * @author wangjian
 * @date 2016/1/11.
 */
public class TabItem implements Serializable {
    public TabItem() {

    }

    public TabItem(String title) {
        this.title = title;
    }

    /**
     * tab中显示的文本 (not null)
     */
    public String title;
}
