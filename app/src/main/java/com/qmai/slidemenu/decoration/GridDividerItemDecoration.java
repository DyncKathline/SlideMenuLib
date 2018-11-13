package com.qmai.slidemenu.decoration;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/*适用于 RecycleView的GridLayoutManager和StaggerGridLayoutManager*/
public class GridDividerItemDecoration extends RecyclerView.ItemDecoration {

    private int[] attrs = {android.R.attr.listDivider};

    private Drawable mDividerDrawable;
    private int mDividerHeight = 20;
    private @ColorInt
    int mDividerColor = Color.RED;
    private Paint mColorPaint;

    public GridDividerItemDecoration(Context context) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs);
        this.mDividerDrawable = typedArray.getDrawable(0);
        typedArray.recycle();
        mColorPaint = new Paint();
        mColorPaint.setColor(mDividerColor);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        drawVertical(c, parent);
        drawHorizontal(c, parent);
    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                if (((GridLayoutManager) layoutManager).getOrientation() == GridLayoutManager.VERTICAL) {
                    int left = child.getLeft() - params.leftMargin - mDividerHeight;
                    int top = child.getBottom() + params.bottomMargin + mDividerHeight;
                    int right = child.getRight() + params.rightMargin + mDividerHeight;
                    int bottom = top + mDividerHeight;
                    //画下面的线
                    mDividerDrawable.setBounds(left, top, right, bottom);
                    mDividerDrawable.draw(c);
                    if (mColorPaint != null) {
                        c.drawRect(left, top, right, bottom, mColorPaint);
                    }

                    //如果是第一行 那么画上面的线
                    if (isFirstRow(parent, i)) {
                        top = child.getTop() - params.topMargin - mDividerHeight;
                        bottom = top + mDividerHeight;
                        mDividerDrawable.setBounds(left, top, right, bottom);
                        mDividerDrawable.draw(c);
                        if (mColorPaint != null) {
                            c.drawRect(left, top, right, bottom, mColorPaint);
                        }
                    }

                    //如果是最后一行 那么画下面的线
                    int spanCount = getSpanCount(parent);
                    if (i >= (childCount - childCount % spanCount)) {
                        top = child.getBottom() - params.topMargin;
                        bottom = top + mDividerHeight;
                        mDividerDrawable.setBounds(left, top, right, bottom);
                        mDividerDrawable.draw(c);
                        if (mColorPaint != null) {
                            c.drawRect(left, top, right, bottom, mColorPaint);
                        }
                    }
                } else {
                    int left = child.getLeft() - params.leftMargin - mDividerHeight;
                    int top = child.getBottom() + params.bottomMargin + mDividerHeight;
                    int right = child.getRight() + params.rightMargin + mDividerHeight;
                    int bottom = top + mDividerHeight;
                    //画下面的线
                    mDividerDrawable.setBounds(left, top, right, bottom);
                    mDividerDrawable.draw(c);
                    if (mColorPaint != null) {
                        c.drawRect(left, top, right, bottom, mColorPaint);
                    }

                    //如果是第一行 那么画上面的线
                    int spanCount = getSpanCount(parent);
                    if (i % spanCount == 0) {
                        top = child.getTop() - params.topMargin - mDividerHeight;
                        bottom = top + mDividerHeight;
                        mDividerDrawable.setBounds(left, top, right, bottom);
                        mDividerDrawable.draw(c);
                        if (mColorPaint != null) {
                            c.drawRect(left, top, right, bottom, mColorPaint);
                        }
                    }
                    //如果是最后一行 那么画下面的线
                    spanCount = getSpanCount(parent);
                    if ((i+1) % spanCount == 0) {
                        top = child.getBottom() - params.topMargin;
                        bottom = top + mDividerHeight;
                        mDividerDrawable.setBounds(left, top, right, bottom);
                        mDividerDrawable.draw(c);
                        if (mColorPaint != null) {
                            c.drawRect(left, top, right, bottom, mColorPaint);
                        }
                    }
                }
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                if (((StaggeredGridLayoutManager) layoutManager).getOrientation() == StaggeredGridLayoutManager.VERTICAL) {
                    int left = child.getLeft() - params.leftMargin - mDividerHeight;
                    int top = child.getBottom() + params.bottomMargin + mDividerHeight;
                    int right = child.getRight() + params.rightMargin + mDividerHeight;
                    int bottom = top + mDividerHeight;
                    //画下面的线
                    mDividerDrawable.setBounds(left, top, right, bottom);
                    mDividerDrawable.draw(c);
                    if (mColorPaint != null) {
                        c.drawRect(left, top, right, bottom, mColorPaint);
                    }

                    //如果是第一行 那么画上面的线
                    if (isFirstRow(parent, i)) {
                        top = child.getTop() - params.topMargin - mDividerHeight;
                        bottom = top + mDividerHeight;
                        mDividerDrawable.setBounds(left, top, right, bottom);
                        mDividerDrawable.draw(c);
                        if (mColorPaint != null) {
                            c.drawRect(left, top, right, bottom, mColorPaint);
                        }
                    }
                } else {

                }
            }

        }
    }

    private void drawVertical(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                if (((GridLayoutManager) layoutManager).getOrientation() == GridLayoutManager.VERTICAL) {
                    int left = child.getRight() + params.rightMargin;
                    int top = child.getTop() - params.topMargin- mDividerHeight;
                    int right = left + mDividerHeight;
                    int bottom = child.getBottom() + params.bottomMargin + mDividerHeight;

                    //画右边的线
                    mDividerDrawable.setBounds(left, top, right, bottom);
                    mDividerDrawable.draw(c);
                    if (mColorPaint != null) {
                        c.drawRect(left, top, right, bottom, mColorPaint);
                    }

                    //如果是第一列 画左边的线
                    if (isFirstColumn(parent, i)) {
                        left = child.getLeft() - params.leftMargin - mDividerHeight;
                        right = left + mDividerHeight;
                        mDividerDrawable.setBounds(left, top, right, bottom);
                        mDividerDrawable.draw(c);
                        if (mColorPaint != null) {
                            c.drawRect(left, top, right, bottom, mColorPaint);
                        }
                    }
                } else {
                    int left = child.getRight() + params.rightMargin;
                    int top = child.getTop() - params.topMargin- mDividerHeight;
                    int right = left + mDividerHeight;
                    int bottom = child.getBottom() + params.bottomMargin + mDividerHeight;

                    //画右边的线
                    mDividerDrawable.setBounds(left, top, right, bottom);
                    mDividerDrawable.draw(c);
                    if (mColorPaint != null) {
                        c.drawRect(left, top, right, bottom, mColorPaint);
                    }

                    //如果是第一列 画左边的线
                    int spanCount = getSpanCount(parent);
                    if (i < spanCount) {
                        left = child.getLeft() - params.leftMargin - mDividerHeight;
                        right = left + mDividerHeight;
                        mDividerDrawable.setBounds(left, top, right, bottom);
                        mDividerDrawable.draw(c);
                        if (mColorPaint != null) {
                            c.drawRect(left, top, right, bottom, mColorPaint);
                        }
                    }
                }
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                if (((StaggeredGridLayoutManager) layoutManager).getOrientation() == StaggeredGridLayoutManager.VERTICAL) {
                    int left = child.getRight() + params.rightMargin;
                    int top = child.getTop() - params.topMargin- mDividerHeight;
                    int right = left + mDividerHeight;
                    int bottom = child.getBottom() + params.bottomMargin + mDividerHeight;

                    //画右边的线
                    mDividerDrawable.setBounds(left, top, right, bottom);
                    mDividerDrawable.draw(c);
                    if (mColorPaint != null) {
                        c.drawRect(left, top, right, bottom, mColorPaint);
                    }

                    //如果是第一列 画左边的线
                    if (isFirstColumn(parent, i)) {
                        left = child.getLeft() - params.leftMargin - mDividerHeight;
                        right = left + mDividerHeight;
                        mDividerDrawable.setBounds(left, top, right, bottom);
                        mDividerDrawable.draw(c);
                        if (mColorPaint != null) {
                            c.drawRect(left, top, right, bottom, mColorPaint);
                        }
                    }
                } else {

                }
            }

        }
    }

    private boolean isFirstColumn(RecyclerView parent, int position) {
        int spanCount = getSpanCount(parent);
        return position % spanCount == 0;
    }

    /*是否是第一行*/
    private boolean isFirstRow(RecyclerView parent, int i) {
        int spanCount = getSpanCount(parent);
        return i <= spanCount - 1;
    }

    private int getSpanCount(RecyclerView parent) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager) layoutManager).getSpanCount();
        }
        return 0;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(mDividerHeight, mDividerHeight, mDividerHeight, mDividerHeight);
    }
}
