package com.qmai.slidemenu.decoration;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/*适用于 RecycleView的LineaLayoutManager*/
public class LinearDividerItemDecoration extends RecyclerView.ItemDecoration {

    private int[] attrs = {android.R.attr.listDivider};

    private Drawable mDividerDrawable;
    private int mDividerHeight = 20;
    private @ColorInt
    int mDividerColor = Color.RED;
    private Paint mColorPaint;
    private int mOri;

    public LinearDividerItemDecoration(Context context, int ori) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs);
        mDividerDrawable = typedArray.getDrawable(0);
        this.mOri = ori;
        typedArray.recycle();
        mColorPaint = new Paint();
        mColorPaint.setColor(mDividerColor);
        mColorPaint.setStrokeWidth(mDividerHeight);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (mOri == LinearLayoutManager.VERTICAL) {
            //画水平线 上下左右的每条分割线都看成一个小矩形，绘制每个item的矩形分割线
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int left = child.getLeft() - params.leftMargin;
                int top = child.getBottom() + params.bottomMargin;
                int right = child.getRight() + params.rightMargin;
                int bottom = top + mDividerHeight;
                mDividerDrawable.setBounds(left, top, right, bottom);
                mDividerDrawable.draw(c);
                if (mColorPaint != null) {
                    c.drawRect(left, top, right, bottom, mColorPaint);
                }
            }
        } else if (mOri == LinearLayoutManager.HORIZONTAL) {
            //画垂直线
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                int left = child.getRight() + params.rightMargin;
                int right = left + mDividerHeight;
                int top = child.getTop() - params.topMargin;
                int bottom = child.getBottom() + params.bottomMargin;
                mDividerDrawable.setBounds(left, top, right, bottom);
                mDividerDrawable.draw(c);
                if (mColorPaint != null) {
                    c.drawRect(left, top, right, bottom, mColorPaint);
                }
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        //获取每个item上下左右四边向外的偏移量 上下左右的每条分割线都看成一个小矩形
        if (mOri == LinearLayoutManager.HORIZONTAL) {
            outRect.set(0, 0, mDividerHeight, 0);
        } else if (mOri == LinearLayoutManager.VERTICAL) {
            outRect.set(0, 0, 0, mDividerHeight);
        }
    }
}
