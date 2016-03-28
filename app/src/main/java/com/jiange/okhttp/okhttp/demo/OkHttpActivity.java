/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.jiange.okhttp.okhttp.demo;

import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jiange.okhttp.flux.ui.BaseActivity;
import com.jiange.okhttp.okhttp.R;
import com.jiange.okhttp.okhttp.demo.controller.LoadController;
import com.jiange.okhttp.okhttp.demo.controller.OKDownController;
import com.jiange.okhttp.okhttp.demo.controller.UploadController;
import com.jiange.okhttp.okhttp.demo.model.LoadResponse;
import com.jiange.okhttp.okhttp.demo.model.PersonResponse;
import com.jiange.okhttp.okhttp.demo.model.UploadResponse;

import java.io.File;
import java.util.List;

import cn.ieclipse.af.util.DialogUtils;

/**
 * 类/接口描述
 *
 * @author wangjian
 * @date 2016/3/23.
 */
public class OkHttpActivity extends BaseActivity implements LoadController.LoadListener,
    UploadController.UploadListener, OKDownController.DownLoadListener {

    LoadController controller;
    UploadController upController;
    OKDownController downController;
    private Button mGet;
    private Button mPost;
    private Button mUpload;
    private Button mDownload;

    private TextView mTextView;
    ProgressBar mProgressBar;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_okhttp;
    }

    @Override
    protected void initContentView(View view) {
        super.initContentView(view);
        mTextView = (TextView) findViewById(R.id.textView);
        mGet = (Button) findViewById(R.id.get);
        mPost = (Button) findViewById(R.id.post);
        mUpload = (Button) findViewById(R.id.upload);
        mDownload = (Button) findViewById(R.id.download);

        mTextView = (TextView) findViewById(R.id.textView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);


        setOnClickListener(mPost, mGet, mUpload, mDownload);
    }

    @Override
    protected void initData() {
        super.initData();
        controller = new LoadController(this);
        upController = new UploadController(this);
        downController = new OKDownController(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == mGet) {
            controller.load();
        }
        else if (v == mPost) {
            controller.post();
        }
        else if (v == mUpload) {
            upController.upload(new File("/storage/emulated/0/DCIM/.thumbnails/1458645728960.jpg"));
        }
        else if (v == mDownload) {
            downController.downLoad();
        }
    }

    @Override
    public void onLoadPersonSuccess(List<PersonResponse> responses) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < responses.size(); i++) {
            String text = responses.get(i).staffname;
            buffer.append(text).append("\n");
        }
        mTextView.setText(buffer.toString());
    }

    @Override
    public void onLoadSuccess(LoadResponse loadResponse) {
        mTextView.setText(loadResponse.title);
    }

    @Override
    public void onLoadFail(String e) {
        DialogUtils.showToast(this, e);
    }

    @Override
    public void loadSuccess(UploadResponse uploadResponse) {
        Toast.makeText(this, "upload success = " + uploadResponse.baseurl, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadFail(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void inDownLoaProgress(float progress, long total) {
        int pro = (int) (progress * 100);
        android.util.Log.e("eee", pro + "");

        mProgressBar.setProgress(pro);
    }

    @Override
    public void onDownLoaSuccess(File file) {
        Toast.makeText(this, "download success = " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

    }
}
