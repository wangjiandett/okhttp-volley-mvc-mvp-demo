package com.jiange.okhttp.flux.flux_lib.dispatcher;

import android.text.TextUtils;

import com.jiange.okhttp.flux.flux_lib.action.Action;
import com.jiange.okhttp.flux.flux_lib.store.Store;

import java.util.ArrayList;
import java.util.List;

/**
 * 事件分发器
 *
 * @author wangjian
 * @date 2016/3/14.
 */
public final class Dispatcher {

    private static Dispatcher instance;
    private List<Store> stores;

    private Dispatcher() {
        stores = new ArrayList<>();
    }

    /**
     * 获取Dispatcher实例
     *
     * @return
     */
    public static Dispatcher getInstance() {
        if (instance == null) {
            synchronized (Dispatcher.class) {
                instance = new Dispatcher();
            }
        }
        return instance;
    }

    /**
     * 将指定store注册到dispatcher中，以实现对store的分发
     *
     * @param store
     */
    public void register(Store store) {
        stores.add(store);
    }

    /**
     * 使用完将指定store移除
     *
     * @param store
     */
    public void unRegister(Store store) {
        stores.remove(store);
    }

    /**
     * 进行事件分发（将action分发到指定store）
     *
     * @param action
     */
    public void dispatch(Action action) {
        if (action == null) {
            throw new IllegalArgumentException("action must not be null");
        }
        if (!TextUtils.isEmpty(action.getType())) {
            for (Store store : stores) {
                store.onAction(action);
            }
        }
        else {
            throw new IllegalArgumentException("the value of action's type must not be null or length is zero");
        }
    }
}
