/*
 * Copyright 2014-2015 ieclipse.cn.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ieclipse.af.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 类/接口描述
 *
 * @author Jamling
 * @date 2015/10/29.
 */
public abstract class AfRecyclerAdapter<T, VH extends AfViewHolder> extends RecyclerView.Adapter {

    private AfDataHolder<T> mDataHolder = new AfDataHolder<>();
    private LayoutInflater mInflater;

    public AfRecyclerAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public T getItem(int position) {
        return mDataHolder.getItem(position);
    }

    public void setDataCheck(int checkMode) {
        mDataHolder.setDataCheck(checkMode);
    }

    public void setDataList(List<T> list) {
        mDataHolder.setDataList(list);
        notifyDataSetChanged();
    }

    public List<T> getDataList() {
        return mDataHolder.getDataList();
    }

    public void add(T data) {
        mDataHolder.add(data);
        notifyItemInserted(mDataHolder.getCount());
    }

    public void add2Top(T data) {
        mDataHolder.add2Top(data);
        notifyItemInserted(0);
    }

    public void add2Top(List<T> list) {
        mDataHolder.addAll2Top(list);
        notifyDataSetChanged();
    }

    public void addAll(List<T> list) {
        mDataHolder.addAll(list);
        notifyDataSetChanged();
    }

    public void updateItem(int position, T data) {
        mDataHolder.getDataList().set(position, data);
        notifyItemChanged(position);
        //notifyItemChanged(msg.arg1, msg.obj);
    }

    public void clearData() {
        mDataHolder.clear();
        notifyDataSetChanged();
    }

    public void clear() {
        mDataHolder.clear();
    }

    /**
     * 函数里面的传入的参数position，
     * 它是在进行onBind操作时确定的，
     * 在删除单项后，已经出现在画面里的项不会再有调用onBind机会，
     * 这样它保留的position一直是未进行删除操作前的postion值。
     * 所以最好使用holder.getAdapterPosition();
     *
     * @param position
     */
    public void deleteItemData(int position) {
        mDataHolder.remove(position);
        // 删除后不为空，更新item
        if (mDataHolder.getCount() > 0) {
            notifyItemRemoved(position);
        }
        else {
            // 删除后为空，更新显示emptyview
            notifyDataSetChanged();
        }
    }

    public abstract int getLayout();

    public abstract VH onBindViewHolder(View view);

    public abstract void onUpdateView(VH holder, T data, int position);

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = mInflater.inflate(getLayout(), parent, false);
        return onBindViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final VH holder = (VH) viewHolder;
        // 绑定数据
        try {
            onUpdateView(holder, getItem(position), position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 设置监听
        if (mOnItemClickLitener != null) {
            holder.setOnClickListener(mOnItemClickLitener);
        }

        if (mOnItemLongClickLitener != null) {
            holder.setOnLongClickListener(mOnItemLongClickLitener);
        }
    }

    @Override
    public int getItemCount() {
        return mDataHolder.getCount();
    }

    //-------------------设置监听-start---------------------//
    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickLitener {
        void onItemLongClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;


    private OnItemLongClickLitener mOnItemLongClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public void setOnItemLongClickLitener(OnItemLongClickLitener mOnItemLongClickLitener) {
        this.mOnItemLongClickLitener = mOnItemLongClickLitener;
    }
    //-------------------设置监听-end---------------------//
}
