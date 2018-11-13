package com.qmai.slidemenu;

import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class MyItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private ItemSwipeListener itemSwipeListener;

    public MyItemTouchHelperCallback(ItemSwipeListener itemSwipeListener) {
        this.itemSwipeListener = itemSwipeListener;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //设置拖动方向和滑动方向
        //上下拖动
        int dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        //左右滑动
        int swipeFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(dragFlag, swipeFlag);
    }

    /*拖拽移动*/
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        if (itemSwipeListener != null) {
            itemSwipeListener.onItemDrag(viewHolder, target);
        }
        return false;
    }

    /*开启长按拖拽*/
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //滑动事件交给外部处理
        if (itemSwipeListener != null) {
            itemSwipeListener.onItemSwipe(viewHolder, direction);
        }
    }

    /*在这里做一些动画效果*/
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        //dX 0-> +(-)viewHolder.itemView.getWidth
        float alpha= 1 - Math.abs(dX) / viewHolder.itemView.getWidth();
        viewHolder.itemView.setAlpha(alpha);
        //产生的布局复用问题 可以在这里处理
        //item滑出删除，那么透明度变化的item的布局可能会被复用
        if(alpha== 0){
            //还原布局之前的状态
            viewHolder.itemView.setAlpha(1);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        //判断选中状态,拖动和滑动的时候 设置item的背景
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            viewHolder.itemView.setBackgroundColor(Color.GRAY);
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    //*这个方法中恢复 item的状态*/
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.setBackgroundColor(Color.WHITE);
        super.clearView(recyclerView, viewHolder);
    }

    public interface ItemSwipeListener {
        void onItemSwipe(RecyclerView.ViewHolder viewHolder, int direction);

        void onItemDrag(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target);
    }
}
