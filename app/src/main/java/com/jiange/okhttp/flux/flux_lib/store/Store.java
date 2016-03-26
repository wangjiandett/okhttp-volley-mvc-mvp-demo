package com.jiange.okhttp.flux.flux_lib.store;


import com.jiange.okhttp.flux.event.BaseStoreChangeEvent;
import com.jiange.okhttp.flux.event.LoadFailEvent;
import com.jiange.okhttp.flux.event.LoadStartEvent;
import com.jiange.okhttp.flux.event.LoadSuccessEvent;
import com.jiange.okhttp.flux.event.OtherEvent;
import com.jiange.okhttp.flux.flux_lib.action.Action;

import org.greenrobot.eventbus.EventBus;


/**
 * Store基类 store中的操作全部是同步的<br\>
 * 可处理数据缓存，数据更新等操作
 *
 * @author wangjian
 * @date 2016/3/14.
 */
public abstract class Store<T extends Action> {
    private EventBus mEventBus;
    private BaseStoreChangeEvent mBaseStoreChangeEvent;
    private T mAction;

    protected Store() {
        mEventBus = EventBus.getDefault();
    }

    /**
     * 将接收界面注册进来
     *
     * @param subscriber
     */
    public void register(Object subscriber) {
        mEventBus.register(subscriber);
    }

    public void unRegister(Object subscriber) {
        mEventBus.unregister(subscriber);
    }

    /**
     * 处理接收到不同的事件，子类需要重写此方法，实现更多功能
     */
    public final void onAction(T action) {
        if (action != null) {
            mAction = action;
            switch (action.getType()) {
                case Action.ACTION_TYPE_LOADING_START:
                    mBaseStoreChangeEvent = new LoadStartEvent();
                    postEvent();
                    return;
                case Action.ACTION_TYPE_LOADING_SUCCESS:
                    mBaseStoreChangeEvent = new LoadSuccessEvent();
                    postEvent();
                    return;
                case Action.ACTION_TYPE_LOADING_FAIL:
                    mBaseStoreChangeEvent = new LoadFailEvent();
                    postEvent();
                    return;
                default:
                    // 此处封装action type接收处可在OtherEvent中获取
                    mBaseStoreChangeEvent = new OtherEvent(mAction.getType());
                    postEvent();
                    return;
            }
        }
    }

    protected T getAction() {
        return mAction;
    }

    private void postEvent() {
        if (mBaseStoreChangeEvent != null) {
            mEventBus.post(mBaseStoreChangeEvent);
        }
    }
}
