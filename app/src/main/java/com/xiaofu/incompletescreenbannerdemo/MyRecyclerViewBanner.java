package com.xiaofu.incompletescreenbannerdemo;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;


import com.xiaofu.incompletescreenbannerdemo.base.MyRecyclerViewBannerBase;

import java.util.List;

public class MyRecyclerViewBanner extends MyRecyclerViewBannerBase<LinearLayoutManager, MyCustomRecyclerAdapter> {

    private MyCustomRecyclerAdapter mMyCustomRecyclerAdapter;

    public MyRecyclerViewBanner(Context context) {
        this(context, null);
    }

    public MyRecyclerViewBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyRecyclerViewBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }


    @Override
    protected void onBannerScrolled(RecyclerView recyclerView, int dx, int dy) {
        //解决连续滑动时指示器不更新的问题
        if (bannerSize < 2) return;
        int firstReal = mLayoutManager.findFirstVisibleItemPosition();
        View viewFirst = mLayoutManager.findViewByPosition(firstReal);
        //当前布局的长度
        int layoutDistance = 0;
        if (LinearLayoutManager.HORIZONTAL == mLayoutManager.getOrientation()) {
            layoutDistance = null == mMyCustomRecyclerAdapter ? 0 : mMyCustomRecyclerAdapter.getItemWidth();
        } else {
            layoutDistance = null == mMyCustomRecyclerAdapter ? 0 : mMyCustomRecyclerAdapter.getItemHeight();
        }
        if (layoutDistance != 0 && viewFirst != null) {
            float viewDistance = 0;
            if (LinearLayoutManager.HORIZONTAL == mLayoutManager.getOrientation()) {
                viewDistance = viewFirst.getRight();
            } else {
                viewDistance = viewFirst.getBottom();
            }
            //item滑动的距离与布局长度的比例
            float ratio = viewDistance / layoutDistance;
            if (ratio > 0.8) {
                if (currentIndex != firstReal) {
                    currentIndex = firstReal;
                    refreshIndicator();
                }
            } else if (ratio < 0.2) {
                if (currentIndex != firstReal + 1) {
                    currentIndex = firstReal + 1;
                    refreshIndicator();
                }
            }
        }
    }

    @Override
    protected void onBannerScrollStateChanged(RecyclerView recyclerView, int newState) {
        int first = mLayoutManager.findFirstVisibleItemPosition();
        int last = mLayoutManager.findLastVisibleItemPosition();
        if (currentIndex != first && first == last) {
            currentIndex = first;
            refreshIndicator();
        }
    }

    @Override
    protected LinearLayoutManager getLayoutManager(Context context, int orientation) {
        return new LinearLayoutManager(context, orientation, false);
    }

    @Override
    protected MyCustomRecyclerAdapter getAdapter(Context context, List<String> list, OnBannerItemClickListener onBannerItemClickListener) {
        mMyCustomRecyclerAdapter = new MyCustomRecyclerAdapter(context, list, onBannerItemClickListener);
        return mMyCustomRecyclerAdapter;
    }


}