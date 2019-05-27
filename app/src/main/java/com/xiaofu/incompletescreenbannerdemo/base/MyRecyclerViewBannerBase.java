package com.xiaofu.incompletescreenbannerdemo.base;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.xiaofu.incompletescreenbannerdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义banner页面的基类
 *
 * @param <L>
 * @param <A>
 */
public abstract class MyRecyclerViewBannerBase<L extends RecyclerView.LayoutManager, A extends MyBaseAdapter> extends FrameLayout {

    protected int autoPlayDuration = 4000;//刷新间隔时间

    protected boolean showIndicator;//是否显示指示器
    protected RecyclerView indicatorContainer;
    protected Drawable mSelectedDrawable;//选择时候导航指示点的资源
    protected Drawable mUnselectedDrawable;//不选择时候导航指示点的资源
    protected IndicatorAdapter indicatorAdapter;
    protected int indicatorMargin;//指示器间距

    protected RecyclerView mRecyclerView;
    protected A adapter;
    protected L mLayoutManager;

    protected boolean hasInit;//是否已经初始化
    protected int bannerSize = 1;
    protected int currentIndex;
    protected boolean isPlaying;

    protected boolean isAutoPlaying;//是否自动播放
    protected List<String> tempUrlList = new ArrayList<>();

    int orientation = 0;

    /**
     * handle的what数值，用于自动播放
     */
    protected int WHAT_AUTO_PLAY = 1000;


    protected Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == WHAT_AUTO_PLAY) {
                ++currentIndex;
                //横向的情况下，位移x族为item的长度
                if (orientation == LinearLayoutManager.HORIZONTAL) {
                    mRecyclerView.smoothScrollBy(adapter.getItemWidth(), 0);
                } else {
                    mRecyclerView.smoothScrollBy(0, adapter.getItemHeight());
                }
                refreshIndicator();
                //发送下一次自动播放的消息
                mHandler.sendEmptyMessageDelayed(WHAT_AUTO_PLAY, autoPlayDuration);

            }
            return false;
        }
    });

    public MyRecyclerViewBannerBase(Context context) {
        this(context, null);
    }

    public MyRecyclerViewBannerBase(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyRecyclerViewBannerBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    protected void initView(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RecyclerViewBannerBase);
        showIndicator = a.getBoolean(R.styleable.RecyclerViewBannerBase_showIndicator, true);
        autoPlayDuration = a.getInt(R.styleable.RecyclerViewBannerBase_interval, 4000);
        isAutoPlaying = a.getBoolean(R.styleable.RecyclerViewBannerBase_autoPlaying, true);
        mSelectedDrawable = a.getDrawable(R.styleable.RecyclerViewBannerBase_indicatorSelectedSrc);
        mUnselectedDrawable = a.getDrawable(R.styleable.RecyclerViewBannerBase_indicatorUnselectedSrc);
        if (mSelectedDrawable == null) {
            //绘制默认选中状态图形
            GradientDrawable selectedGradientDrawable = new GradientDrawable();
            selectedGradientDrawable.setShape(GradientDrawable.OVAL);
            selectedGradientDrawable.setColor(Color.RED);
            selectedGradientDrawable.setSize(dp2px(5), dp2px(5));
            selectedGradientDrawable.setCornerRadius(dp2px(5) / 2);
            mSelectedDrawable = new LayerDrawable(new Drawable[]{selectedGradientDrawable});
        }
        if (mUnselectedDrawable == null) {
            //绘制默认未选中状态图形
            GradientDrawable unSelectedGradientDrawable = new GradientDrawable();
            unSelectedGradientDrawable.setShape(GradientDrawable.OVAL);
            unSelectedGradientDrawable.setColor(Color.GRAY);
            unSelectedGradientDrawable.setSize(dp2px(5), dp2px(5));
            unSelectedGradientDrawable.setCornerRadius(dp2px(5) / 2);
            mUnselectedDrawable = new LayerDrawable(new Drawable[]{unSelectedGradientDrawable});
        }

        indicatorMargin = a.getDimensionPixelSize(R.styleable.RecyclerViewBannerBase_indicatorSpace, dp2px(4));
        int marginLeft = a.getDimensionPixelSize(R.styleable.RecyclerViewBannerBase_indicatorMarginLeft, dp2px(16));
        int marginRight = a.getDimensionPixelSize(R.styleable.RecyclerViewBannerBase_indicatorMarginRight, dp2px(0));
        int marginBottom = a.getDimensionPixelSize(R.styleable.RecyclerViewBannerBase_indicatorMarginBottom, dp2px(11));
        int g = a.getInt(R.styleable.RecyclerViewBannerBase_indicatorGravity, 0);
        int gravity;
        if (g == 0) {
            gravity = GravityCompat.START;
        } else if (g == 2) {
            gravity = GravityCompat.END;
        } else {
            gravity = Gravity.CENTER;
        }
        int o = a.getInt(R.styleable.RecyclerViewBannerBase_orientation, 0);
        if (o == 0) {
            orientation = LinearLayoutManager.HORIZONTAL;
        } else if (o == 1) {
            orientation = LinearLayoutManager.VERTICAL;
        }
        a.recycle();

        //recyclerView部分
        mRecyclerView = new RecyclerView(context);
        //将SnapHelper辅助类与recycleview绑定一起，重点！！！
        new MyCustomSnapHelper().attachToRecyclerView(mRecyclerView);
        mLayoutManager = getLayoutManager(context, orientation);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                onBannerScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                onBannerScrollStateChanged(recyclerView, newState);

            }
        });
        LayoutParams vpLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mRecyclerView, vpLayoutParams);

        //指示器部分
        indicatorContainer = new RecyclerView(context);

        LinearLayoutManager indicatorLayoutManager = new LinearLayoutManager(context, orientation, false);
        indicatorContainer.setLayoutManager(indicatorLayoutManager);
        indicatorAdapter = new IndicatorAdapter();
        indicatorContainer.setAdapter(indicatorAdapter);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | gravity;
        params.setMargins(marginLeft, 0, marginRight, marginBottom);
        addView(indicatorContainer, params);
        if (!showIndicator) {
            indicatorContainer.setVisibility(GONE);
        }
    }

    /**
     * banner被拖动
     *
     * @param recyclerView
     * @param dx
     * @param dy
     */
    protected void onBannerScrolled(RecyclerView recyclerView, int dx, int dy) {

    }

    /**
     * banner拖动状态改变
     *
     * @param recyclerView
     * @param newState
     */
    protected void onBannerScrollStateChanged(RecyclerView recyclerView, int newState) {

    }

    protected abstract L getLayoutManager(Context context, int orientation);

    protected abstract A getAdapter(Context context, List<String> list, OnBannerItemClickListener onBannerItemClickListener);

    /**
     * 设置轮播间隔时间
     *
     * @param millisecond 时间毫秒
     */
    public void setIndicatorInterval(int millisecond) {
        this.autoPlayDuration = millisecond;
    }

    /**
     * 设置是否自动播放（上锁）
     *
     * @param playing 开始播放
     */
    protected synchronized void setPlaying(boolean playing) {
        if (isAutoPlaying && hasInit) {
            if (!isPlaying && playing) {
                mHandler.sendEmptyMessageDelayed(WHAT_AUTO_PLAY, autoPlayDuration);
                isPlaying = true;
            } else if (isPlaying && !playing) {
                mHandler.removeMessages(WHAT_AUTO_PLAY);
                isPlaying = false;
            }
        }
    }

    /**
     * 设置是否禁止滚动播放
     */
    public void setAutoPlaying(boolean isAutoPlaying) {
        this.isAutoPlaying = isAutoPlaying;
        setPlaying(this.isAutoPlaying);
    }

    /**
     * 返回是否在轮播中
     *
     * @return
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     * 设置是否展示导航指示器
     *
     * @param showIndicator
     */
    public void setShowIndicator(boolean showIndicator) {
        this.showIndicator = showIndicator;
        indicatorContainer.setVisibility(showIndicator ? VISIBLE : GONE);
    }

    /**
     * 设置轮播数据集
     */
    public void initBannerImageView(@NonNull List<String> newList, OnBannerItemClickListener onBannerItemClickListener) {
        //解决recyclerView嵌套问题
        if (compareListDifferent(newList, tempUrlList)) {
            hasInit = false;
            setVisibility(VISIBLE);
            setPlaying(false);
            adapter = getAdapter(getContext(), newList, onBannerItemClickListener);
            mRecyclerView.setAdapter(adapter);
            tempUrlList = newList;
            bannerSize = tempUrlList.size();
            if (bannerSize > 1) {
                indicatorContainer.setVisibility(VISIBLE);
                //所谓的无限轮播，就是把数据重复显示到一个很大的position位置，总有循环完的一天，只是这个时间很长，基本可以忽略
                currentIndex = bannerSize * 10000;
                mRecyclerView.scrollToPosition(currentIndex);
                indicatorAdapter.notifyDataSetChanged();
                setPlaying(true);
            } else {
                indicatorContainer.setVisibility(GONE);
                currentIndex = 0;
            }
            hasInit = true;
        }
        if (!showIndicator) {
            indicatorContainer.setVisibility(GONE);
        }
    }

    /**
     * 设置轮播数据集
     */
    public void initBannerImageView(@NonNull List<String> newList) {
        initBannerImageView(newList, null);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //监听用户手势，用户对banner有拉动点击等动作时候，停止自动轮播，用户无动作后再恢复轮播
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setPlaying(false);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                setPlaying(true);
                break;
        }
        //解决recyclerView嵌套问题
        try {
            return super.dispatchTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    //解决recyclerView嵌套问题
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    //解决recyclerView嵌套问题
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //页面加载附属后开启轮播
        setPlaying(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //页面要退出时候停止轮播
        setPlaying(false);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            //页面显示的时候开启轮播
            setPlaying(true);
        } else {
            //页面隐藏的时候关闭轮播
            setPlaying(false);
        }
    }

    /**
     * 标示点适配器
     */
    protected class IndicatorAdapter extends RecyclerView.Adapter {

        int currentPosition = 0;

        public void setPosition(int currentPosition) {
            this.currentPosition = currentPosition;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            ImageView bannerPoint = new ImageView(getContext());
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(indicatorMargin, indicatorMargin, indicatorMargin, indicatorMargin);
            bannerPoint.setLayoutParams(lp);
            return new RecyclerView.ViewHolder(bannerPoint) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ImageView bannerPoint = (ImageView) holder.itemView;
            bannerPoint.setImageDrawable(currentPosition == position ? mSelectedDrawable : mUnselectedDrawable);

        }

        @Override
        public int getItemCount() {
            return bannerSize;
        }
    }


    /**
     * 刷新改变导航的指示点
     */
    protected synchronized void refreshIndicator() {
        if (showIndicator && bannerSize > 1) {
            indicatorAdapter.setPosition(currentIndex % bannerSize);
            indicatorAdapter.notifyDataSetChanged();
        }
    }

    public interface OnBannerItemClickListener {
        void onItemClick(int position);
    }

    /**
     * dp转px
     *
     * @param dp
     * @return
     */
    protected int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }

    /**
     * 获取颜色
     */
    protected int getColor(@ColorRes int color) {
        return ContextCompat.getColor(getContext(), color);
    }

    /**
     * 对比两个list数据是否不同
     *
     * @param newTabList
     * @param oldTabList
     * @return
     */
    protected boolean compareListDifferent(List<String> newTabList, List<String> oldTabList) {
        if (oldTabList == null || oldTabList.isEmpty())
            return true;
        if (newTabList.size() != oldTabList.size())
            return true;
        for (int i = 0; i < newTabList.size(); i++) {
            if (TextUtils.isEmpty(newTabList.get(i)))
                return true;
            if (!newTabList.get(i).equals(oldTabList.get(i))) {
                return true;
            }
        }
        return false;
    }

}