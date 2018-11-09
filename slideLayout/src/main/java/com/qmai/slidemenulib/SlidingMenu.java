package com.qmai.slidemenulib;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class SlidingMenu extends ViewGroup {
    private static final String TAG = "SlidingMenu";
    private Scroller mScroller;//弹性滑动
    public static boolean isIntercept = false;
    private VelocityTracker mVelocityTracker;//速度跟踪器
    private int mTouchSlop;//被判定为滑动的最小距离
    private int mScaleTouchSlop;//为了处理单击事件的冲突
    private int mMaxVelocity;//计算滑动速度用
    private int mScrollTime = 500;
    private int mSnapVelocity = 600;

    private float mFirstX;

    private float mFirstY;

    private float mLastX;

    private float mLastY;

    private int menuWidth;//菜单的宽度

    private int contentWidth;//内容的宽度

    private View menu;//菜单

    private View content;//内容
    private float mMove;

    public SlidingMenu(Context context) {
        this(context, null);
    }

    public SlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTouchSlop = ViewConfiguration.getTouchSlop();
        mScroller = new Scroller(getContext());
        mVelocityTracker = VelocityTracker.obtain();
        mScaleTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mMaxVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
    }

    private boolean ensureChildren(){
        int childCount = getChildCount();

        if(childCount!=2)
            return false;

        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(!ensureChildren()) {
            throw new RuntimeException("Only two child is allowed.");
        }
        //测量所有的子view
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(TAG, "l = " + l + "....." + "t = " + t + "....." + "r = " + r + "....." + "b = " + b);
        if(!ensureChildren()) {
            throw new RuntimeException("Only two child is allowed.");
        }
        menu = getChildAt(0);
        content = getChildAt(1);
        menuWidth = menu.getMeasuredWidth();
        contentWidth = content.getMeasuredWidth();
        menu.layout(-1 * menuWidth, 0, 0, menu.getMeasuredHeight());
        content.layout(0, 0, contentWidth, content.getMeasuredHeight());
        Log.d(TAG, "content.getMeasuredWidth() = " + content.getMeasuredWidth() + "-----" + menuWidth);
        Log.d(TAG, "content.getMeasuredHeight() = " + content.getMeasuredHeight() + "----" + menu.getMeasuredHeight());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onInterceptTouchEvent.........ACTION_DOWN");
                mFirstX = mLastX = ev.getX();
                mFirstY = mLastY = ev.getY();
                isIntercept = false;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onInterceptTouchEvent.........ACTION_MOVE" + ev.getX() + "-->" + mFirstX + "-->" + mLastX);
                float deltaX = ev.getX() - mLastX;
                float deltaY = ev.getY() - mLastY;
                mMove = ev.getX() - mFirstX;
                Log.d(TAG, "onInterceptTouchEvent..ACTION_MOVE..mMove:" + mMove + "..deltaX:" + deltaX  + "..deltaY:" + deltaY);
                if (mMove > 0 && Math.abs(deltaX) > mTouchSlop && Math.abs(deltaX) > Math.abs(deltaY)) {
                    Log.d(TAG, "onInterceptTouchEvent.........ACTION_MOVE-----右滑动");
                    isIntercept = true;
                } else if (mMove < 0 && Math.abs(deltaX) > mTouchSlop && Math.abs(deltaX) > Math.abs(deltaY)) {
                    Log.d(TAG, "onInterceptTouchEvent.........ACTION_MOVE-----左滑动");
                    isIntercept = false;
                } else if (Math.abs(deltaX) > mTouchSlop && Math.abs(deltaX) < Math.abs(deltaY)) {
                    isIntercept = false;
                }
//                if(Math.abs(deltaX) > mTouchSlop && Math.abs(deltaX) > Math.abs(deltaY)){
//                    Log.d("----------","执行了此事件");
//                    isIntercept=true;
//                }


        }
        return isIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVelocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onTouchEvent.........ACTION_DOWN");
                return true;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onTouchEvent.........ACTION_MOVE");
                Log.d(TAG, "getScrollX() = " + getScrollX());
                float deltaX = event.getX() - mLastX;
                float newScrollX = getScrollX() - deltaX;
                if (newScrollX < -menuWidth) newScrollX = -menuWidth;
                if (newScrollX > 0) newScrollX = 0;
                scrollTo((int) newScrollX, 0);
                mLastX = event.getX();
                mLastY = event.getY();
                break;
            case MotionEvent.ACTION_UP: //根据滚动的距离判断怎么还原
                Log.d(TAG, "onTouchEvent.........ACTION_UP");
                mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                int vx = (int) Math.abs(mVelocityTracker.getXVelocity());
                mVelocityTracker.clear();
                Log.d(TAG, "vx = " + vx);
                //快速滑动的处理,未必一定是200
                if(vx > mSnapVelocity){
                    if(event.getX() - mFirstX > 0){ //向右快速滑动
                        showMenuComplete();
                        return true;
                    }else{ //向左快速滑动
                        hideMenuComplete();
                        return true;
                    }
                }
                //正常滑动的处理
                //当滑动距离小于Menu宽度的一半时，平滑滑动到主页面
                if (getScrollX() > -menuWidth / 2) {
                    hideMenuComplete();
                    return true;
                } else {
                    //当滑动距离大于Menu宽度的一半时，平滑滑动到Menu页面
                    showMenuComplete();
                    return true;
                }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 完全隐藏menu
     */
    private void hideMenuComplete() {
        mScroller.startScroll(getScrollX(), 0, 0 - getScrollX(), 0, mScrollTime);
        invalidate();
    }

    /**
     * 完全展示menu
     */
    private void showMenuComplete() {
        mScroller.startScroll(getScrollX(), 0, -menuWidth - getScrollX(), 0, mScrollTime);
        invalidate();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    public interface SlidingListener {
        boolean slidingInterceptLeft();

        boolean slidingInterceptRight();
    }

    private SlidingListener slidingListener;

    public void setSlidingListener(SlidingListener listener) {
        slidingListener = listener;
    }
}
