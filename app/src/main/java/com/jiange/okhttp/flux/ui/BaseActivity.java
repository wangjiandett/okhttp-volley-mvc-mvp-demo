/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.jiange.okhttp.flux.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.jiange.okhttp.okhttp.R;
import com.jiange.okhttp.utils.LoadDialogUtils;
import com.jiange.okhttp.volley.VolleyUtils;

import cn.ieclipse.af.app.AfActivity;
import cn.ieclipse.af.util.AppUtils;
import cn.ieclipse.af.util.KeyboardUtils;
import cn.ieclipse.af.volley.RestError;

/**
 * 类/接口描述
 *
 * @author Harry
 * @date 2016/1/27.
 */
public abstract class BaseActivity extends AfActivity implements View.OnClickListener {
    public static final String EXTRA_BUNDLE = "bundle";

    protected TextView mTitleLeftView;
    protected TextView mTitleTextView;

    @Override
    public void onClick(View v) {
        if (v == mTitleLeftView) { // default back.
            finish();
        }
    }

    protected void initIntent(Bundle bundle) {

    }

    @Override
    protected void initWindowFeature() {
        super.initWindowFeature();
        setWindowBackground(android.R.color.white);
        setImmersiveMode(true);
    }

    protected void initContentView(View view) {

    }

    protected void initHeaderView() {
        mTitleLeftView = (TextView) View.inflate(this, R.layout.title_left_tv, null);
        mTitleTextView = (TextView) View.inflate(this, R.layout.title_middle_tv, null);

        mTitleBar.setLeft(mTitleLeftView);
        mTitleBar.setMiddle(mTitleTextView);

        int padding = AppUtils.dimen2px(this, R.dimen.activity_horizontal_margin);
        mTitleBar.setPadding(padding, 0, padding, 0);
        if(!isOverlay()){
            mTitleBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mTitleBar.setBottomDrawable(getResources().getColor(R.color.divider_white_bg));
        }
        setOnClickListener(mTitleLeftView);

    }

    protected void initData() {

    }

    protected void initBottomView() {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        KeyboardUtils.autoHideSoftInput(this, ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected int getStatusBarColor() {
        return getResources().getColor(R.color.colorPrimary);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private Dialog mLoadingDialog;

    public void showLoadingDialog() {
        showLoadingDialog(getString(R.string.common_loading));
    }

    public void showLoadingDialog(String message) {
        hideLoadingDialog();
        mLoadingDialog = LoadDialogUtils.showLoadingDialog(this, message);
    }

    public void hideLoadingDialog() {
        LoadDialogUtils.hideLodingDialog(mLoadingDialog);
    }

    public void toastError(RestError error){
        VolleyUtils.toastError(this, error);
    }
}
