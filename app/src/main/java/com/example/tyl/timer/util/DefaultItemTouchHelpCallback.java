package com.example.tyl.timer.util;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by TYL on 2017/6/19.
 */

public class DefaultItemTouchHelpCallback extends ItemTouchHelper.Callback {
    /**
     * Item操作回调
     */
    private OnItemTouchCallbackListener mOnItemTouchCallbackListener;
    /**
     * 是否可以拖拽
     */
    private boolean isCanDrag = false;                  //这里设置为不可拖拽
    /**
     * 是否可以滑动
     */

    private boolean isCanSwip = true;                   //这里设置为可以滑动

    public DefaultItemTouchHelpCallback(OnItemTouchCallbackListener onItemTouchCallbackListener) {
        this.mOnItemTouchCallbackListener = onItemTouchCallbackListener;
    }

    /**
     * 设置Item 操作的回调，去更新UI和数据库
     * @param onItemTouchCallbackListener
     */

    public void setOnItemTouchCallbackListener(OnItemTouchCallbackListener onItemTouchCallbackListener) {
        this.mOnItemTouchCallbackListener = onItemTouchCallbackListener;
    }

    /**
     * 设置是否可以被拖拽
     * @param CanDrag   是true，否false
     */



    /**
     * 设置是否可以被滑动
     * @param canSwip   是 true，否 false
     */
    public void setSwipEnable(boolean   canSwip) {
        isCanSwip = canSwip;
    }

    /**
     * 当Item被长按的时候是否可以被拖拽
     * @return
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return isCanDrag;
    }

    /**
     * Item是否可以被滑动（H：左右滑动，V：上下滑动）
     * @return
     */

    @Override
    public boolean isItemViewSwipeEnabled() {
        return isCanSwip;
    }

    /**
     * 当用户拖拽或者滑动Item的时候需要我们高告诉系统滑动或者拖拽的方向
     * @param recyclerView
     * @param viewHolder
     * @return
     */

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

            int dragFlag = 0;
            int     swipFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

            return makeMovementFlags(dragFlag, swipFlag);
    }
    /**
     * 当Item被拖拽的时候回调
     * @param recyclerView  recyclerView
     * @param srcViewHolder    拖拽的ViewHolder
     * @param targetViewHolder        目的地的ViewHolder
     * @return
     */

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder srcViewHolder, RecyclerView.ViewHolder targetViewHolder) {
//        if (mOnItemTouchCallbackListener != null) {
//            return mOnItemTouchCallbackListener.onMove(srcViewHolder.getAdapterPosition(), targetViewHolder.getAdapterPosition());
//        }
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (mOnItemTouchCallbackListener != null) {
            mOnItemTouchCallbackListener.onSwiped(viewHolder.getAdapterPosition());
        }
    }
        public interface OnItemTouchCallbackListener{
            /**
             * 当某个Item被滑动删除的时候
             * @param adapterPosition
             */
            void onSwiped(int adapterPosition);

        }
}
