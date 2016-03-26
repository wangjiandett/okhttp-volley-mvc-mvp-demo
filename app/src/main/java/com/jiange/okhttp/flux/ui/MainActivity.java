package com.jiange.okhttp.flux.ui;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jiange.okhttp.flux.action.LoadActionCreator;
import com.jiange.okhttp.flux.event.LoadFailEvent;
import com.jiange.okhttp.flux.event.LoadStartEvent;
import com.jiange.okhttp.flux.event.LoadSuccessEvent;
import com.jiange.okhttp.flux.event.OtherEvent;
import com.jiange.okhttp.flux.flux_lib.dispatcher.Dispatcher;
import com.jiange.okhttp.flux.store.LoadStore;
import com.jiange.okhttp.okhttp.R;
import com.jiange.okhttp.volley.request.HouseRequest;
import com.jiange.okhttp.volley.request.HouseResponse;

import org.greenrobot.eventbus.Subscribe;

import cn.ieclipse.af.adapter.AfBaseAdapter;
import cn.ieclipse.af.volley.RestError;

public class MainActivity extends BaseActivity {

    private LoadActionCreator mLoadActionCreator;
    private LoadStore mLoadStore;
    private Dispatcher mDispatcher;

    private ListView mListView;
    MyAdapter myAdapter;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initContentView(View view) {
        super.initContentView(view);
        mListView = (ListView) findViewById(R.id.list);
        myAdapter = new MyAdapter();
        mListView.setAdapter(myAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mLoadActionCreator.createAction(new LoadStore.LoadAction("--" + position));
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        mDispatcher = Dispatcher.getInstance();
        mLoadStore = new LoadStore();
        mLoadActionCreator = new LoadActionCreator(mDispatcher, new LoadActionCreator.LoginListener() {
            @Override
            public void onLoginCodeSuccess(HouseResponse out) {

            }

            @Override
            public void onLoginCodeFail(RestError error) {

            }
        });

        mLoadStore.register(this);
        mDispatcher.register(mLoadStore);

        loadData();
    }

    private void loadData() {
        mLoadActionCreator.load(new HouseRequest());
    }

    // 加载开始
    @Subscribe
    public void onLoadStartEvent(LoadStartEvent startEvent) {
        showLoadingDialog();
    }

    // 加载成功
    @Subscribe
    public void onLoadSuccessEvent(LoadSuccessEvent successEvent) {
        hideLoadingDialog();
        HouseResponse response = mLoadStore.getData();
        myAdapter.setDataList(response.regionOption);
        myAdapter.notifyDataSetChanged();
    }

    // 加载失败
    @Subscribe
    public void onLoadErrorEvent(LoadFailEvent failEvent) {
        toastError(mLoadStore.getError());
        hideLoadingDialog();
    }

    // 其他event
    @Subscribe
    public void onOtherEvent(OtherEvent otherEvent) {
        String type = mLoadStore.getLoadAction().getType();
        Toast.makeText(this, otherEvent.getType(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLoadStore.unRegister(this);
        mDispatcher.unRegister(mLoadStore);
    }

    /***************************
     * setdata
     **************************/

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
