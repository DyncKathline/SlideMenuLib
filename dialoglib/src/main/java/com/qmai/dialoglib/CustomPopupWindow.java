package com.qmai.dialoglib;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.annotation.FloatRange;
import androidx.annotation.IntDef;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.core.widget.PopupWindowCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 可以在任意位置显示的PopupWindow
 * 用法：
 CustomPopupWindow.Builder builder = new CustomPopupWindow.Builder(this);
 builder
 .setContentView(R.layout.dialog)
 .setBackgroundDrawable(true)
 .setLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
 .setOnInitListener(new CustomPopupWindow.Builder.OnInitListener() {
@Override
public void init(CustomPopupWindow popupWindow, View view) {
popupWindow.showAsDropDown(btnPopupWindow);
}
})
 .build();
 */
public class CustomPopupWindow extends PopupWindow {
    protected Builder builder;

    //下面的几个变量只是位置处理外部点击事件（6.0以上）
    //是否只是获取宽高
    //getViewTreeObserver监听时
    private boolean isOnlyGetWH = true;
    private View mAnchorView;
    @VerticalPosition
    private int mVerticalGravity = VerticalPosition.BELOW;
    @HorizontalPosition
    private int mHorizontalGravity = HorizontalPosition.LEFT;
    private int mOffsetX;
    private int mOffsetY;

    protected CustomPopupWindow(Context context, Builder builder) {
        super(context);
        this.builder = builder;
        init();
    }

    private void init() {
        setContentView(builder.contentView);
        if (builder.width != 0 && builder.height != 0) {
            setHeight(builder.height);
            setWidth(builder.width);
        }
        if (builder.backgroundDrawableable) {
            setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }else {
            setBackgroundDrawable(null);
        }
        if (!builder.cancelable) {
            setFocusable(true);
            setOutsideTouchable(false);
        } else {
            setFocusable(true);
            setOutsideTouchable(true);
            getContentView().setFocusable(true);
            getContentView().setFocusableInTouchMode(true);
            getContentView().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dismiss();
                        return true;
                    }
                    return false;
                }
            });
            //在Android 6.0以上 ，只能通过拦截事件来解决
            setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (isOutOfBounds(builder.context, event)) {
                        if(builder.onTouchOutsideListener != null) {
                            builder.onTouchOutsideListener.touchOutSide();
                        }
                    }
                    return false;
                }
            });
        }
        if (builder.animId != -1) {
            setAnimationStyle(builder.animId);
        }
        if(builder.onInitListener != null) {
            builder.onInitListener.init(this, builder.contentView);
        }
    }

    /**
     * 判断当前用户触摸是否超出了Dialog的显示区域
     *
     * @param context
     * @param event
     * @return
     */
    private boolean isOutOfBounds(Context context, MotionEvent event) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();

        if ((event.getAction() == MotionEvent.ACTION_DOWN)
                && ((x < 0) || (x >= builder.width) || (y < 0) || (y >= builder.height))) {
            //outside
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            //outside
            return true;
        }
        return false;
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        isOnlyGetWH = true;
        mAnchorView = parent;
        mOffsetX = x;
        mOffsetY = y;
        addGlobalLayoutListener(getContentView());
        super.showAtLocation(parent, gravity, x, y);
    }

    public void showAtAnchorView(@NonNull View anchorView, @VerticalPosition int verticalPos, @HorizontalPosition int horizontalPos) {
        showAtAnchorView(anchorView, verticalPos, horizontalPos, true);
    }

    public void showAtAnchorView(@NonNull View anchorView, @VerticalPosition int verticalPos, @HorizontalPosition int horizontalPos, boolean fitInScreen) {
        showAtAnchorView(anchorView, verticalPos, horizontalPos, 0, 0, fitInScreen);
    }
    public void showAtAnchorView(@NonNull View anchorView, @VerticalPosition int verticalPos, @HorizontalPosition int horizontalPos, int x, int y) {
        showAtAnchorView(anchorView, verticalPos, horizontalPos, x, y, true);
    }

    public void showAtAnchorView(@NonNull View anchorView, @VerticalPosition int verticalPos, @HorizontalPosition int horizontalPos, int x, int y, boolean fitInScreen) {
        isOnlyGetWH = false;
        mAnchorView = anchorView;
        mOffsetX = x;
        mOffsetY = y;
        mVerticalGravity = verticalPos;
        mHorizontalGravity = horizontalPos;
        showBackgroundAnimator();
        final View contentView = getContentView();
        addGlobalLayoutListener(contentView);
        setClippingEnabled(fitInScreen);
        contentView.measure(makeDropDownMeasureSpec(getWidth()), makeDropDownMeasureSpec(getHeight()));
        final int measuredW = contentView.getMeasuredWidth();
        final int measuredH = contentView.getMeasuredHeight();
        if (!fitInScreen) {
            final int[] anchorLocation = new int[2];
            anchorView.getLocationInWindow(anchorLocation);
            x += anchorLocation[0];
            y += anchorLocation[1] + anchorView.getHeight();
        }
        y = calculateY(anchorView, verticalPos, measuredH, y);
        x = calculateX(anchorView, horizontalPos, measuredW, x);
        if (fitInScreen) {
            PopupWindowCompat.showAsDropDown(this, anchorView, x, y, Gravity.NO_GRAVITY);
        } else {
            showAtLocation(anchorView, Gravity.NO_GRAVITY, x, y);
        }
    }

    /**
     * 根据垂直gravity计算y偏移
     */
    private int calculateY(View anchor, int verticalGravity, int measuredH, int y) {
        switch (verticalGravity) {
            case VerticalPosition.ABOVE:
                y -= measuredH + anchor.getHeight();
                break;
            case VerticalPosition.ALIGN_BOTTOM:
                y -= measuredH;
                break;
            case VerticalPosition.CENTER:
                y -= anchor.getHeight() / 2 + measuredH / 2;
                break;
            case VerticalPosition.ALIGN_TOP:
                y -= anchor.getHeight();
                break;
            case VerticalPosition.BELOW:
                // Default position.
                break;
        }

        return y;
    }

    /**
     * 根据水平gravity计算x偏移
     */
    private int calculateX(View anchor, int horizontalGravity, int measuredW, int x) {
        switch (horizontalGravity) {
            case HorizontalPosition.LEFT:
                x -= measuredW;
                break;
            case HorizontalPosition.ALIGN_RIGHT:
                x -= measuredW - anchor.getWidth();
                break;
            case HorizontalPosition.CENTER:
                x += anchor.getWidth() / 2 - measuredW / 2;
                break;
            case HorizontalPosition.ALIGN_LEFT:
                // Default position.
                break;
            case HorizontalPosition.RIGHT:
                x += anchor.getWidth();
                break;
        }

        return x;
    }

    @SuppressWarnings("ResourceType")
    private static int makeDropDownMeasureSpec(int measureSpec) {
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), getDropDownMeasureSpecMode(measureSpec));
    }

    private static int getDropDownMeasureSpecMode(int measureSpec) {
        switch (measureSpec) {
            case ViewGroup.LayoutParams.WRAP_CONTENT:
                return View.MeasureSpec.UNSPECIFIED;
            default:
                return View.MeasureSpec.EXACTLY;
        }
    }

    //监听器，用于PopupWindow弹出时获取准确的宽高
    private final ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            builder.width = getContentView().getWidth();
            builder.height = getContentView().getHeight();
            //只获取宽高时，不执行更新操作
            if (isOnlyGetWH) {
                removeGlobalLayoutListener();
                return;
            }
            updateLocation(builder.width, builder.height, mAnchorView, mVerticalGravity, mHorizontalGravity, mOffsetX, mOffsetY);
            removeGlobalLayoutListener();
        }
    };

    private void updateLocation(int width, int height, @NonNull View anchor,
                                @VerticalPosition final int verticalGravity,
                                @HorizontalPosition int horizontalGravity,
                                int x, int y) {
        x = calculateX(anchor, horizontalGravity, width, x);
        y = calculateY(anchor, verticalGravity, height, y);
        update(anchor, x, y, width, height);
    }

    private void removeGlobalLayoutListener() {
        if (getContentView() != null) {
            if (Build.VERSION.SDK_INT >= 16) {
                getContentView().getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
            } else {
                getContentView().getViewTreeObserver().removeGlobalOnLayoutListener(mOnGlobalLayoutListener);
            }
        }
    }

    private void addGlobalLayoutListener(View contentView) {
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        dismissBackgroundAnimator();
        removeGlobalLayoutListener();
    }

    /**
     * 窗口显示，窗口背景透明度渐变动画
     */
    private void showBackgroundAnimator() {
        if (builder.mAlpha >= 1f) return;
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, builder.mAlpha);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (float) animation.getAnimatedValue();
                setWindowBackgroundAlpha(alpha);
            }
        });
        animator.setDuration(360);
        animator.start();
    }

    /**
     * 窗口隐藏，窗口背景透明度渐变动画
     */
    private void dismissBackgroundAnimator() {
        if (builder.mAlpha >= 1f) return;
        ValueAnimator animator = ValueAnimator.ofFloat(builder.mAlpha, 1.0f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (float) animation.getAnimatedValue();
                setWindowBackgroundAlpha(alpha);
            }
        });
        animator.setDuration(360);
        animator.start();
    }

    /**
     * 控制窗口背景的不透明度
     */
    private void setWindowBackgroundAlpha(float alpha) {
        if (builder.context == null) return;
        if (builder.context instanceof Activity) {
            Window window = ((Activity) builder.context).getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.alpha = alpha;
            window.setAttributes(layoutParams);
        }
    }

    public static class Builder {
        private int width;
        private int height;
        private float mAlpha;
        private Context context;
        private View contentView;
        private boolean cancelable;
        private int animId;
        public boolean backgroundDrawableable;
        private OnInitListener onInitListener;
        private OnTouchOutsideListener onTouchOutsideListener;
        public interface OnInitListener {
            /**
             * 绑定控件
             *
             * @param popupWindow
             * @param view
             */
            void init(CustomPopupWindow popupWindow, View view);
        }

        public interface OnTouchOutsideListener{
            void touchOutSide();
        }

        public Builder(final Context context) {
            width = ViewGroup.LayoutParams.WRAP_CONTENT;
            height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mAlpha = 1f; //背景灰度  0-1  1表示全透明
            this.context = context;
            contentView = LayoutInflater.from(this.context).inflate(android.R.layout.select_dialog_item, null);
            cancelable = true;   //点击外部消失
            animId = -1;
            backgroundDrawableable = true;
        }

        public Builder setContentView(View view) {
            contentView = view;
            return this;
        }

        public Builder setContentView(@LayoutRes int layoutId) {
            contentView = LayoutInflater.from(context).inflate(layoutId, null);
            return this;
        }

        /**
         * Creates a new set of layout parameters with the specified width
         * and height.
         *
         * @param width  the width, either set WindowManager.LayoutParams.WRAP_CONTENT or
         *               WindowManager.LayoutParams.FILL_PARENT (replaced by WindowManager.LayoutParams.MATCH_PARENT in
         *               API Level 8), or a fixed size in pixels
         * @param height the height, either set WindowManager.LayoutParams.WRAP_CONTENT or
         *               WindowManager.LayoutParams.FILL_PARENT (replaced by WindowManager.LayoutParams.MATCH_PARENT in
         *               API Level 8), or a fixed size in pixels
         * @return
         */
        public Builder setLayoutParams(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        /**
         * 设置PopupWindow弹出和Dialog退出的动画
         *
         * @param animId
         * @return
         */
        public Builder setAnimId(int animId) {
            this.animId = animId;
            return this;
        }

        /**
         * 设置Dialog之外的背景透明度，0~1之间，默认值 0.5f，半透明，越小也透明
         *
         * @param alpha
         * @return
         */
        public Builder setDimAmount(@FloatRange(from = 0, to = 1.0)float alpha) {
            mAlpha = alpha;
            return this;
        }

        /**
         * 是否给Dialog的背景设置透明，默认true
         *
         * @param backgroundDrawableable
         * @return
         */
        public Builder setBackgroundDrawable(boolean backgroundDrawableable) {
            this.backgroundDrawableable = backgroundDrawableable;
            return this;
        }

        /**
         * 设置PopupWindow是否可以关闭在Dialog之外的区域，默认true
         *
         * @param cancelable
         * @return
         */
        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        /**
         * 设置子View
         *
         * @param listener OnInitListener
         * @return Builder
         */
        public Builder setOnInitListener(OnInitListener listener) {
            onInitListener = listener;
            return this;
        }

        public Builder setOnTouchOutsideListener(OnTouchOutsideListener listener){
            onTouchOutsideListener = listener;
            return this;
        }

        /**
         * 创建PopupWindow
         * @return
         */
        public CustomPopupWindow build() {
            CustomPopupWindow popupWindow = new CustomPopupWindow(context, this);
            return popupWindow;
        }
    }

    @IntDef({
            HorizontalPosition.CENTER,
            HorizontalPosition.LEFT,
            HorizontalPosition.RIGHT,
            HorizontalPosition.ALIGN_LEFT,
            HorizontalPosition.ALIGN_RIGHT,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface HorizontalPosition {
        int CENTER = 0;
        int LEFT = 1;
        int RIGHT = 2;
        int ALIGN_LEFT = 3;
        int ALIGN_RIGHT = 4;
    }

    @IntDef({
            VerticalPosition.CENTER,
            VerticalPosition.ABOVE,
            VerticalPosition.BELOW,
            VerticalPosition.ALIGN_TOP,
            VerticalPosition.ALIGN_BOTTOM,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface VerticalPosition {
        int CENTER = 0;
        int ABOVE = 1;
        int BELOW = 2;
        int ALIGN_TOP = 3;
        int ALIGN_BOTTOM = 4;
    }
}
