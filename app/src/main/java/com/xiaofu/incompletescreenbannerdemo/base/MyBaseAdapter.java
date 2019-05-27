package com.xiaofu.incompletescreenbannerdemo.base;

import android.support.v7.widget.RecyclerView;

/**
 * @description： 对适配器封装一层，增加返回item的宽度与高度的方法
 * @author: created by XiaoFu on 2019/5/26
 */
public abstract class MyBaseAdapter<N extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<N> {
    public abstract int getItemWidth();
    public abstract int getItemHeight();

    @Override
    public void onBindViewHolder(N holder, int position) {

    }
}
