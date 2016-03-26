/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package cn.ieclipse.af.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.List;

import cn.ieclipse.af.R;
import cn.ieclipse.af.adapter.AfRecyclerAdapter;
import cn.ieclipse.af.view.recycle.DividerGridItemDecoration;
import cn.ieclipse.af.view.recycle.DividerLine;
import cn.ieclipse.af.view.recycle.RefreshEmptyView;
import cn.ieclipse.af.view.refresh.SwipyRefreshLayout;
import cn.ieclipse.af.view.refresh.SwipyRefreshLayoutDirection;

/**
 * 封装recyclerview，只需设置一个recyclerview resid 或recyclerview对象；
 * 注：(recyclerview显示scrollbar需要在布局中设置android:scrollbars="vertical");
 * 即可完成对recyclerview的上拉加载，下拉刷新，局部刷新，基本的属性设置等功能。
 * 更多自定义的功能请重写Recyclerview进行封装,以满足特殊需求
 *
 * @author wangjian
 * @date 2015/12/25.
 */
public class RefreshRecyclerView<T> extends LinearLayout implements View.OnClickListener,
    SwipyRefreshLayout.OnRefreshListener {

    private static String TAG = RefreshRecyclerView.class.getSimpleName();

    private void log(String msg) {
        android.util.Log.e(TAG, msg);
    }

    /**
     * 当前的ItemDecoration
     */
    private RecyclerView.ItemDecoration mItemDecoration;
    private SwipyRefreshLayout mSwipyRefreshLayout;
    private RecyclerView mRecyclerView;
    /**
     * 存放自定义emptyview的布局
     */
    private FrameLayout mEmptyLayout;
    /**
     * 存放mEmptyLayout的上层布局
     */
    private RefreshEmptyView mEmptyView;
    /**
     * RecyclerView 当前manager
     */
    private LinearLayoutManager mCurrentLayoutManager = null;
    /**
     * 最后一项的索引
     */
    private int mLastVisibleItem;
    /**
     * 是否允许自动loadmore
     */
    private boolean mAutoLoadEnable;
    /**
     * 默认分隔线颜色
     */
    private int mDividerColor = 0xffd8d8d8;
    /**
     * 默认分隔线高度
     */
    private int mDividerHeight = 1;
    /**
     * recyclerview 布局id
     */
    private int mRecyclerViewId = 0;
    /**
     * 默认list分隔线
     */
    private DividerLine mListDecoration;
    /**
     * 正在加载状态时显示的view
     */
    private View mLoadingView;
    /**
     * 正在加载完成时显示的view
     */
    private View mTipView;
    /**
     * adapter
     */
    private AfRecyclerAdapter mAdapter;
    /**
     * 默认刷新方向
     */
    private SwipyRefreshLayoutDirection mCurrentDirection = SwipyRefreshLayoutDirection.TOP;
    /**
     * 分页第一页索引
     */
    private static final int PAGE_FIRST = 1;
    /**
     * 分页加载每一页的大小
     */
    private static final int PAGE_SIZE = 10;
    /**
     * 分页加载当前页数 mCurrentPage
     */
    private int mCurrentPage = PAGE_FIRST;
    /**
     * adapter中数据条目
     */
    private int mItemCount = 0;
    private int mPageSize = PAGE_SIZE;

    public RefreshRecyclerView(Context context) {
        this(context, null);
    }

    public RefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        initAttr(context, attrs);
    }

    private void initView() {
        if (mSwipyRefreshLayout == null) {
            // 用于存放mRecyclerView
            mSwipyRefreshLayout = new SwipyRefreshLayout(getContext());
            mSwipyRefreshLayout.setOnRefreshListener(this);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
            // SwipyRefreshLayout中添加recyclerview
            mSwipyRefreshLayout.addView(mRecyclerView, params);

            // 设置默认布局管理器
            setLinearLayoutManager(LinearLayoutManager.VERTICAL);
            // 设置emptyview
            mEmptyView = new RefreshEmptyView(getContext());
            // 数据为空时只有下拉加载
            mEmptyView.setDirection(SwipyRefreshLayoutDirection.TOP);
            mEmptyLayout = mEmptyView.getParentView();
            // mEmptyLayout = new LinearLayout(getContext());
            // 添加view
            addView(mSwipyRefreshLayout);
            addView(mEmptyView, getLayoutCenterLyParams());
            mSwipyRefreshLayout.setVisibility(GONE);

            // 是否允许自动加载
            setAutoLoadEnable(mAutoLoadEnable);
            observeRecycleViewStatus();
        }
    }

    /**
     * 设置recyclerview，此方需要显示调用，或在xml中配置recyclerView属性
     *
     * @param recyclerView 要显示的recyclerView
     */
    public void setRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        if (mRecyclerView != null) {
            initView();
        }
    }

    /**
     * 获取设置的recyclerView
     *
     * @return
     */
    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    private void initAttr(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RefreshRecyclerView);
            mDividerColor = array.getColor(R.styleable.RefreshRecyclerView_dividerColor, Color.TRANSPARENT);
            mDividerHeight = array.getDimensionPixelOffset(R.styleable.RefreshRecyclerView_dividerHeight,
                mDividerHeight);
            mAutoLoadEnable = array.getBoolean(R.styleable.RefreshRecyclerView_autoLoad, mAutoLoadEnable);
            mRecyclerViewId = array.getResourceId(R.styleable.RefreshRecyclerView_recyclerView, mRecyclerViewId);
            array.recycle();
        }

        // 初始化xml中获取的recyclerview
        if (mRecyclerViewId > 0) {
            mRecyclerView = (RecyclerView) View.inflate(getContext(), mRecyclerViewId, null);
            setRecyclerView(mRecyclerView);
        }
    }

    /**
     * 获取RecyclerView
     *
     * @return
     */
    public RecyclerView getRefreshView() {
        return mRecyclerView;
    }

    public SwipyRefreshLayout getSwipyRefreshLayout() {
        return mSwipyRefreshLayout;
    }

    /**
     * 设置刷新方向
     *
     * @param direction {@link SwipyRefreshLayoutDirection}
     */
    public void setRefreshDirection(SwipyRefreshLayoutDirection direction) {
        mSwipyRefreshLayout.setDirection(direction);
    }

    /**
     * 设置要显示的empty view
     *
     * @param loadingView 正在加载状态的view
     * @param tipView     加载完成后没有数据的view
     */
    public void setEmptyView(View loadingView, View tipView) {
        if (mEmptyLayout != null) {
            mLoadingView = loadingView;
            mTipView = tipView;

            mEmptyLayout.addView(mLoadingView, getLayoutCenterLyParams());
            mEmptyLayout.addView(mTipView, getLayoutCenterLyParams());

            emptyViewIsLoading(true);

            mTipView.setOnClickListener(this);
            mEmptyView.setOnRefreshListener(emptyViewRefreshListener);
            // setDataObserver();
        }
    }

    /**
     * empty 是否正在加载切换 mLoadingView和mTipView的显示状态
     *
     * @param isloading
     */
    private void emptyViewIsLoading(boolean isloading) {
        if (mLoadingView != null && mTipView != null) {
            mLoadingView.setVisibility(isloading ? View.VISIBLE : View.GONE);
            mTipView.setVisibility(isloading ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * empty view 的下拉监听
     */
    private SwipyRefreshLayout.OnRefreshListener emptyViewRefreshListener = new SwipyRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh(SwipyRefreshLayoutDirection direction) {
            // empty view 刷新触发mAfRecyclerView的刷新事件
            if (mOnRefreshListener != null) {
                emptyViewIsLoading(true);
                // 触发RecyclerView的下拉刷新方法
                RefreshRecyclerView.this.onRefresh(SwipyRefreshLayoutDirection.TOP);
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (v == mTipView && mOnRefreshListener != null) {
            // 处理empty view 显示的是mTipView时的点击事件
            mEmptyView.setRefreshing(true);
            emptyViewRefreshListener.onRefresh(SwipyRefreshLayoutDirection.TOP);
        }
    }

    private LinearLayout.LayoutParams getLayoutCenterLyParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        return params;
    }

    /**
     * 添加adapter监听
     */
    private void setDataObserver() {
        if (mAdapter != null) {
            mAdapter.registerAdapterDataObserver(mEmptyObserver);
            mEmptyObserver.onChanged();
        }
    }

    /**
     * 监听adapter中数据的变化
     */
    private RecyclerView.AdapterDataObserver mEmptyObserver = new RecyclerView.AdapterDataObserver() {

        @Override
        public void onChanged() {
            if (mAdapter != null && mEmptyLayout != null) {
                //View currentView = getCurrentView();
                if (mAdapter.getItemCount() <= 0 && mEmptyLayout.getVisibility() == GONE) {
                    // listview数据为空时显示empty view
                    mEmptyLayout.setVisibility(VISIBLE);
                    mSwipyRefreshLayout.setVisibility(GONE);
                    mRecyclerView.setVisibility(GONE);
                }
                else if (mAdapter.getItemCount() > 0 && mSwipyRefreshLayout.getVisibility() == GONE) {
                    // 当前是mEmptyView时切数据不为空显示listview
                    mEmptyLayout.setVisibility(GONE);
                    mSwipyRefreshLayout.setVisibility(VISIBLE);
                    mRecyclerView.setVisibility(VISIBLE);
                }
            }
        }
    };

    /**
     * 设置RecyclerView adapter
     *
     * @param adapter
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        if (adapter != null && mRecyclerView != null) {
            mRecyclerView.setAdapter(adapter);
            if (adapter instanceof AfRecyclerAdapter) {
                mAdapter = (AfRecyclerAdapter) adapter;
                setDataObserver();
            }
        }
    }

    /**
     * 设置刷新indicator的颜色，默认黑色
     *
     * @param colorResIds
     */
    public void setColorSchemeResources(int... colorResIds) {
        mSwipyRefreshLayout.setColorSchemeResources(colorResIds);
        if (mEmptyView != null) {
            mEmptyView.setColorSchemeResources(colorResIds);
        }
    }

    /**
     * 更新ItemDecoration
     *
     * @param decoration
     */
    private void removeOldItemDecoration(RecyclerView.ItemDecoration decoration) {
        if (mItemDecoration != null) {
            mRecyclerView.removeItemDecoration(mItemDecoration);
        }
        mItemDecoration = decoration;
        mRecyclerView.addItemDecoration(mItemDecoration);
    }

    /**
     * 设置分隔线颜色,实际是设置Paint中的{@link android.graphics.Paint setColor(int)}的颜色，仅在布局管理器是LinearLayoutManager有效
     *
     * @param color
     */
    public void setDividerColor(int color) {
        this.mDividerColor = color;
        if (mListDecoration != null) {
            mListDecoration.setColor(color);
        }
    }

    /**
     * 设置分隔线的宽度， 仅在布局管理器是LinearLayoutManager有效
     *
     * @param height
     */
    public void setDividerHeight(int height) {
        this.mDividerHeight = height;
        if (mListDecoration != null) {
            mListDecoration.setSize(height);
        }
    }

    /**
     * 设置AfRecycleView默认样式
     *
     * @param orientation LinearLayout.VERTICAL
     *                    LinearLayout.HORIZONTAL
     */
    public void setLinearLayoutManager(int orientation) {
        // 设置布局样式
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(orientation);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // 设置分隔线
        mListDecoration = new DividerLine(orientation);
        setDividerHeight(mDividerHeight);
        setDividerColor(mDividerColor);

        //DividerItemDecoration decoration = new DividerItemDecoration(getContext(), orientation);
        removeOldItemDecoration(mListDecoration);

        mRecyclerView.setLayoutManager(manager);
    }

    /**
     * 显示成gridview形式
     *
     * @param column
     */
    public void setGridLayoutManager(int column) {
        GridLayoutManager manager = new GridLayoutManager(getContext(), column);
        DividerGridItemDecoration decoration = new DividerGridItemDecoration(getContext());
        removeOldItemDecoration(decoration);
        mRecyclerView.setLayoutManager(manager);
    }

    /**
     * 显示成gridview形式 支持水平和垂直方向
     *
     * @param column      列数
     * @param orientation LinearLayout.VERTICAL
     *                    LinearLayout.HORIZONTAL
     */
    public void setGridLayoutManager(int column, int orientation) {
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(column, orientation);
        DividerGridItemDecoration decoration = new DividerGridItemDecoration(getContext());
        removeOldItemDecoration(decoration);
        mRecyclerView.setLayoutManager(manager);
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.mOnRefreshListener = onRefreshListener;
    }

    /**
     * 是否支持滑动到底部自动加载(仅在RecyclerView布局管理器为LinearLayoutManager且方向为Vertical才有效)
     *
     * @param enable
     */
    public void setAutoLoadEnable(boolean enable) {
        this.mAutoLoadEnable = enable;
    }

    /**
     * RecyclerView添加滚动监听
     */
    private void observeRecycleViewStatus() {
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            mCurrentLayoutManager = (LinearLayoutManager) layoutManager;
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItem + 1 == mAdapter.getItemCount()) {
                        // mRecyclerView滚动到最后一条，切停止滚动 同时没有在刷新或加载
                        boolean isRefreshing = mSwipyRefreshLayout.isRefreshing();
                        if (mAutoLoadEnable && !isRefreshing) {
                            log("auto load more");
                            mSwipyRefreshLayout.setRefreshing(true);
                            RefreshRecyclerView.this.onRefresh(SwipyRefreshLayoutDirection.BOTTOM);
                        }
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    mLastVisibleItem = mCurrentLayoutManager.findLastVisibleItemPosition();
                }
            });
        }
    }


    //------------------------update item start----------------------------

    /**
     * 更新item
     *
     * @param item
     */
    public void updateItem(T item) {
        if (mAdapter != null) {
            int idx = getChooseItemIndex(item);
            log("try to refresh item idx = " + idx);
            if (idx >= 0) {
                mAdapter.updateItem(idx, item);
            }
        }
    }

    /**
     * 删除item
     *
     * @param item
     */
    public void deleteItem(T item) {
        if (mAdapter != null) {
            int idx = getChooseItemIndex(item);
            log("try to delete item idx= " + idx);
            if (idx >= 0) {
                mAdapter.deleteItemData(idx);
            }
        }
    }

    /**
     * adapter 中的T 必须重写equals()
     *
     * @param item
     * @return
     */
    private int getChooseItemIndex(T item) {
        int idx = -1;
        if (mAdapter != null && item != null) {
            for (int i = 0; i < mAdapter.getDataList().size(); i++) {
                if (item.equals(mAdapter.getItem(i))) {
                    idx = i;
                    return idx;
                }
            }
            return idx;
        }
        return idx;
    }

    /**
     * 加载完成将数据添加到adapter中
     *
     * @param list 数据
     */
    public void onLoadFinish(List<T> list) {
        if (mAdapter != null) {
            // 当direction == top 或 null时默认是刷新操作
            if (SwipyRefreshLayoutDirection.TOP == mCurrentDirection) {
                mAdapter.getDataList().clear();
            }
            else {//if (SwipyRefreshLayoutDirection.BOTTOM == direction)
                // do nothing
            }
            mAdapter.addAll(list);
        }
        finishLoad();
    }

    /**
     * 加载失败，重置RecyclerView的加载状态
     */
    public void onLoadFailure() {
        finishLoad();
    }

    /**
     * 加载完成清除view的下拉或上拉刷新的状态
     */
    private void finishLoad() {
        // 清除RecyclerView的加载状态
        if (mSwipyRefreshLayout.isRefreshing()) {
            mSwipyRefreshLayout.setRefreshing(false);
        }

        // 清除empty的加载状态
        if (mEmptyView.isRefreshing()) {
            mEmptyView.setRefreshing(false);
        }
        emptyViewIsLoading(false);
    }

    /**
     * 获取当前需要加载的页数
     *
     * @return
     */
    public int getCurrentPage() {
        return mCurrentPage;
    }

    /**
     * 计算页码
     */
    private void calcCurrentPage() {
        if (mAdapter != null) {
            mItemCount = mAdapter.getItemCount();
            int p = mItemCount / mPageSize;
            if (mItemCount % mPageSize >= 0) {
                mCurrentPage = p + 1;
            }
            else {
                mCurrentPage = p;
            }
        }
        if (mCurrentPage <= 0) {
            mCurrentPage = PAGE_FIRST;
        }
        log("current page : " + mCurrentPage + ", item count : " + mItemCount);
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        if (mOnRefreshListener != null) {
            log("onRefresh current direction = " + direction);
            // 下拉刷新执行的操作
            if (direction == SwipyRefreshLayoutDirection.TOP) {
                mCurrentDirection = SwipyRefreshLayoutDirection.TOP;
                mCurrentPage = PAGE_FIRST;
                mOnRefreshListener.onRefresh();
            }
            else {
                //加载更多执行
                mCurrentDirection = SwipyRefreshLayoutDirection.BOTTOM;
                // 计算当前加载页数
                calcCurrentPage();
                mOnRefreshListener.onLoadMore();
            }
        }
    }

    private OnRefreshListener mOnRefreshListener;

    public interface OnRefreshListener {
        void onRefresh();

        void onLoadMore();
    }
}
