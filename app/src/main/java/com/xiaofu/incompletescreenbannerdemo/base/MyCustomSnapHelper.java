package com.xiaofu.incompletescreenbannerdemo.base;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;

/**
 * @description： SnapHelper工具类，辅助recycleview滑动item后保证item完整显示位置在左侧
 * @author: created by XiaoFu on 2019/5/26
 */
public class MyCustomSnapHelper extends SnapHelper {


    /**重写此方法以捕捉目标视图中的特定点或任何轴上的容器视图。
     * @param layoutManager
     * @param targetView
     * @return 输出坐标将结果放入。out [0]是水平轴上的距离，out [1]是垂直轴上的距离。
     */
    @Nullable
    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager,
                                              @NonNull View targetView) {
        int[] out = new int[2];
        //处理横向移动情况
        if (layoutManager.canScrollHorizontally()) {
            out[0] = distanceToStart(layoutManager, targetView,
                    getHorizontalHelper(layoutManager));
        } else {
            out[0] = 0;
        }

        //处理竖向移动情况
        if (layoutManager.canScrollVertically()) {
            out[1] = distanceToStart(layoutManager, targetView,
                    getVerticalHelper(layoutManager));
        } else {
            out[1] = 0;
        }
        return out;
    }

    /**重写此方法以提供捕捉的特定目标视图。
     * @param layoutManager
     * @return
     */
    @Nullable
    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager.canScrollVertically()) {
            return findStartView(layoutManager, getVerticalHelper(layoutManager));
        } else if (layoutManager.canScrollHorizontally()) {
            return findStartView(layoutManager, getHorizontalHelper(layoutManager));
        }
        return null;
    }

    /**覆盖以提供特定的适配器目标位置以进行捕捉。
     * @param layoutManager
     * @param velocityX
     * @param velocityY
     * @return
     */
    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX,
                                      int velocityY) {
        final int itemCount = layoutManager.getItemCount();
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION;
        }

        View mStartMostChildView = null;
        if (layoutManager.canScrollVertically()) {
            mStartMostChildView = findStartView(layoutManager, getVerticalHelper(layoutManager));
        } else if (layoutManager.canScrollHorizontally()) {
            mStartMostChildView = findStartView(layoutManager, getHorizontalHelper(layoutManager));
        }

        if (mStartMostChildView == null) {
            return RecyclerView.NO_POSITION;
        }

        //根据得出的第一个item获取当前的Position
        final int centerPosition = layoutManager.getPosition(mStartMostChildView);
        if (centerPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION;
        }

        //判断是否需要向前获取一个目标，因为centerPosition的前一个目标有可能有一部分出现在界面上
        final boolean forwardDirection;
        if (layoutManager.canScrollHorizontally()) {
            forwardDirection = velocityX > 0;
        } else {
            forwardDirection = velocityY > 0;
        }

        //判断是否为颠倒的布局属性
        boolean reverseLayout = false;
        if ((layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            RecyclerView.SmoothScroller.ScrollVectorProvider vectorProvider =
                    (RecyclerView.SmoothScroller.ScrollVectorProvider) layoutManager;
            PointF vectorForEnd = vectorProvider.computeScrollVectorForPosition(itemCount - 1);
            if (vectorForEnd != null) {
                reverseLayout = vectorForEnd.x < 0 || vectorForEnd.y < 0;
            }
        }
        return reverseLayout
                ? (forwardDirection ? centerPosition - 1 : centerPosition)
                : (forwardDirection ? centerPosition + 1 : centerPosition);
    }


    /**计算滑动中的item返回到左侧的距离
     * @param layoutManager
     * @param targetView
     * @param helper
     * @return
     */
    private int distanceToStart(@NonNull RecyclerView.LayoutManager layoutManager,
                                @NonNull View targetView, OrientationHelper helper) {
        //获取item当前start位置数据
        final int childStart = helper.getDecoratedStart(targetView);
        //item的外层容器的start位置数据
        final int containerStart;
        if (layoutManager.getClipToPadding()) {
            //布局有padding数据的情况下
            containerStart = helper.getStartAfterPadding();
        } else {
            containerStart = 0;
        }
        //两数相减，得出需要移动item的距离
        return childStart - containerStart;
    }

    /**
     * Return the child view that is currently closest to the start of this parent.
     *
     * @param layoutManager The {@link RecyclerView.LayoutManager} associated with the attached
     *                      {@link RecyclerView}.
     * @param helper The relevant {@link OrientationHelper} for the attached {@link RecyclerView}.
     *
     * @return the child view that is currently closest to the center of this parent.
     */
    @Nullable
    private View findStartView(RecyclerView.LayoutManager layoutManager,
                                OrientationHelper helper) {
        int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return null;
        }

        View closestChild = null;
        final int start;
        if (layoutManager.getClipToPadding()) {
            start = helper.getStartAfterPadding();
        } else {
            start = 0;
        }
        int absClosest = Integer.MAX_VALUE;

        for (int i = 0; i < childCount; i++) {
            final View child = layoutManager.getChildAt(i);
            int childStart = helper.getDecoratedStart(child);
            int absDistance = Math.abs(childStart - start);

            /** if child start is closer than previous closest, set it as closest  **/
            if (absDistance < absClosest) {
                absClosest = absDistance;
                closestChild = child;
            }
        }
        return closestChild;
    }


    @NonNull
    private OrientationHelper getVerticalHelper(@NonNull RecyclerView.LayoutManager layoutManager) {
           return OrientationHelper.createVerticalHelper(layoutManager);
    }

    @NonNull
    private OrientationHelper getHorizontalHelper(
            @NonNull RecyclerView.LayoutManager layoutManager) {
        return OrientationHelper.createHorizontalHelper(layoutManager);
    }
}

