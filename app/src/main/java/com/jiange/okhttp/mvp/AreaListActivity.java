/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.jiange.okhttp.mvp;

import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jiange.okhttp.flux.ui.BaseActivity;
import com.jiange.okhttp.mvp.persenter.AreaPersenterImp;
import com.jiange.okhttp.mvp.view.AreaView;
import com.jiange.okhttp.okhttp.R;
import com.jiange.okhttp.volley.request.HouseResponse;

import cn.ieclipse.af.adapter.AfBaseAdapter;

/**
 * 类/接口描述
 *
 * @author wangjian
 * @date 2016/3/16.
 */
public class AreaListActivity extends BaseActivity implements AreaView {

    ListView mListView;
    MyAdapter myAdapter;
    AreaPersenterImp areaPersenterImp;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        super.initData();
        areaPersenterImp = new AreaPersenterImp(this);
        areaPersenterImp.loadAreaList();
    }

    @Override
    protected void initContentView(View view) {
        super.initContentView(view);
        mListView = (ListView) findViewById(R.id.list);
        myAdapter = new MyAdapter();
        mListView.setAdapter(myAdapter);
    }

    @Override
    public void loadSuccess(HouseResponse response) {
        hideLoadingDialog();
        myAdapter.setDataList(response.regionOption);
        myAdapter.notifyDataSetChanged();
    }

    @Override
    public void loadFail(String msg) {
        hideLoadingDialog();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgress() {
        showLoadingDialog();
    }

    class MyAdapter extends AfBaseAdapter<HouseResponse.Item> {
        @Override
        public int getLayout() {
            return android.R.layout.simple_list_item_1;
        }

        @Override
        public void onUpdateView(View convertView, int position) {
            TextView textView = (TextView) convertView;
            textView.setText(getItem(position).label);
        }
    }
}
