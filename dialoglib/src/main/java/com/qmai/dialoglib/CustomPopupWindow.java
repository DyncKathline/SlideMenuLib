package com.qmai.dialoglib;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.FloatRange;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * Created by KathLine on 2016/11/17.</br>
 * <p>使用说明</p>
 * <pre>
 CustomPopupWindow.Builder builder = new CustomPopupWindow.Builder(this);
 builder
 .setBackgroundDrawable(true)
 .setLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
 .setViewListener(new CustomPopupWindow.Builder.OnInitListener() {
@Override
public void init(CustomPopupWindow popupWindow, View view) {

popupWindow.showAsDropDown(xxx);
}
})
 .build();
 </pre>
 */
public class CustomPopupWindow extends PopupWindow {
    protected Builder builder;

    @Override
    public void dismiss() {
        super.dismiss();
        Window window = ((Activity) builder.context).getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.alpha = 1.0f;
        window.setAttributes(params);
    }

    /**
     *
     * @param anchor
     * @param layoutGravity
     * @param xmerge
     * @param ymerge
     */
    public void showBashOfAnchor(View anchor, LayoutGravity layoutGravity, int xmerge, int ymerge) {
        int[] offset=layoutGravity.getOffset(anchor, this);
        showAsDropDown(anchor, offset[0]+xmerge, offset[1]+ymerge);
    }

    /**
     * showAsDropDown是显示在参照物anchor的周围，
     * xoff、yoff分别是X轴、Y轴的偏移量，如果不设
     * 置xoff、yoff，默认是显示在anchor的下方
     * @param anchor
     * @param xoff
     * @param yoff
     */
    public void showDropDown(View anchor, int xoff, int yoff) {
        showAsDropDown(anchor, xoff, yoff);
    }

    /**
     * showAtLocation是设置在父控件的位置，如设置
     * Gravity.BOTTOM表示在父控件底部弹出，xoff、
     * yoff也是X轴、Y轴的偏移量
     * @param parent
     * @param gravity
     * @param x
     * @param y
     */
    public void showLocation(View parent, int gravity, int x, int y) {
        showAtLocation(parent, gravity, x, y);
    }

    private CustomPopupWindow(Context context, Builder builder) {
        super(context);
        this.builder = builder;
        View view = LayoutInflater.from(context).inflate(builder.layoutId, null);
        setContentView(view);
        if(builder.backgroundDrawableable) {
            setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置透明背景
        }
        setOutsideTouchable(builder.isTouchable);//设置outside可点击
        setFocusable(builder.isTouchable);//设置outside可点击
        Window window = ((Activity) context).getWindow();
        if (builder.width == 0 || builder.height == 0) {
            //如果没设置宽高，默认是WRAP_CONTENT
            setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
            setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        } else if(builder.isFullScreen) {
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏，即没有系统状态栏
        } else {
            setWidth(builder.width);
            setHeight(builder.height);
        }
        //设置背景
        WindowManager.LayoutParams params = window.getAttributes();
        if (builder.offsetX != 0) {
            params.x = builder.offsetX;
        }
        if (builder.offsetY != 0) {
            params.y = builder.offsetY;
        }
        params.alpha = builder.dimAmount;
        window.setAttributes(params);
        if (builder.animId != 0) {
            setAnimationStyle(builder.animId);
        }
        if (builder.listener != null && builder.layoutId != 0) {
            builder.listener.init(this, view);
        }
    }

    public static class Builder {
        public Context context;
        public int layoutId;
        public int animId;
        public boolean backgroundDrawableable;
        public float dimAmount;
        public boolean isTouchable;
        public boolean isFullScreen;
        public int offsetX;
        public int offsetY;
        public int width;
        public int height;
        public OnInitListener listener;

        public Builder(Context context) {
            this.context = context;
            layoutId = android.R.layout.select_dialog_item;
            animId = 0;
            backgroundDrawableable = false;
            dimAmount = 0.5f;
            isTouchable = true;
            offsetX = 0;
            offsetY = 0;
            width = 0;
            height = 0;
        }

        /**
         * @param layoutId 设置PopupWindow 布局ID
         * @return Builder
         */
        public Builder setContentView(@LayoutRes int layoutId) {
            this.layoutId = layoutId;
            return this;
        }

        /**
         * 设置Dialog弹出和Dialog退出的动画
         *
         * @param animId
         * @return
         */
        public Builder setAnimId(int animId) {
            this.animId = animId;
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
         * 设置Dialog的位置<br>
         * lp.x与lp.y表示相对于原始位置的偏移.
         * 当参数值包含Gravity.LEFT时,对话框出现在左边,所以lp.x就表示相对左边的偏移,负值无效.
         * 当参数值包含Gravity.RIGHT时,对话框出现在右边,所以lp.x就表示相对右边的偏移,负值无效.
         * 当参数值包含Gravity.TOP时,对话框出现在上边,所以lp.y就表示相对上边的偏移,负值无效.
         * 当参数值包含Gravity.BOTTOM时,对话框出现在下边,所以lp.y就表示相对下边的偏移,负值无效.
         * 当参数值包含Gravity.CENTER_HORIZONTAL时
         * ,对话框水平居中,所以lp.x就表示在水平居中的位置移动lp.x像素,正值向右移动,负值向左移动.
         * 当参数值包含Gravity.CENTER_VERTICAL时
         * ,对话框垂直居中,所以lp.y就表示在垂直居中的位置移动lp.y像素,正值向右移动,负值向左移动.
         * gravity的默认值为Gravity.CENTER
         *
         * @param x x小于0左移，大于0右移
         * @param y y小于0上移，大于0下移
         * @return
         */
        public Builder setOffset(int x, int y) {
            this.offsetX = x;
            this.offsetY = y;
            return this;
        }

        /**
         * 是否给Dialog的背景设置透明，默认false
         *
         * @param backgroundDrawableable
         * @return
         */
        public Builder setBackgroundDrawable(boolean backgroundDrawableable) {
            this.backgroundDrawableable = backgroundDrawableable;
            return this;
        }

        /**
         * 设置Dialog之外的背景透明度，0~1之间，默认值 0.5f，半透明
         *
         * @param dimAmount
         * @return
         */
        public Builder setDimAmount(@FloatRange(from = 0, to = 1.0) float dimAmount) {
            this.dimAmount = dimAmount;
            return this;
        }

        /**
         * 设置Dialog是否可以关闭在Dialog之外的区域，默认true
         *
         * @param isTouchable
         * @return
         */
        public Builder setisTouchable(boolean isTouchable) {
            this.isTouchable = isTouchable;
            return this;
        }

        /**
         * 是否设置全屏模式，指的是去除系统状态栏，默认不去除
         *
         * @param isFullScreen
         * @return
         */
        public Builder setFullScreen(boolean isFullScreen) {
            this.isFullScreen = isFullScreen;
            return this;
        }

        /**
         * 设置子View
         *
         * @param listener OnInitListener
         * @return Builder
         */
        public Builder setViewListener(OnInitListener listener) {
            this.listener = listener;
            return this;
        }

        public interface OnInitListener {
            /**
             * 绑定控件
             *
             * @param popupWindow
             * @param view
             */
            void init(CustomPopupWindow popupWindow, View view);
        }

        public CustomPopupWindow build() {
            return new CustomPopupWindow(context, this);
        }

    }

    public static class LayoutGravity {
        private int layoutGravity;
        // waring, don't change the order of these constants!
        public static final int ALIGN_LEFT=0x1;
        public static final int ALIGN_ABOVE=0x2;
        public static final int ALIGN_RIGHT=0x4;
        public static final int ALIGN_BOTTOM=0x8;
        public static final int TO_LEFT=0x10;
        public static final int TO_ABOVE=0x20;
        public static final int TO_RIGHT=0x40;
        public static final int TO_BOTTOM=0x80;
        public static final int CENTER_HORI=0x100;
        public static final int CENTER_VERT=0x200;

        public LayoutGravity(int gravity) {
            layoutGravity=gravity;
        }

        public int getLayoutGravity() { return layoutGravity; }
        public void setLayoutGravity(int gravity) { layoutGravity=gravity; }

        public void setHoriGravity(int gravity) {
            layoutGravity&=(0x2+0x8+0x20+0x80+0x200);
            layoutGravity|=gravity;
        }
        public void setVertGravity(int gravity) {
            layoutGravity&=(0x1+0x4+0x10+0x40+0x100);
            layoutGravity|=gravity;
        }

        public boolean isParamFit(int param) {
            return (layoutGravity & param) > 0;
        }

        public int getHoriParam() {
            for(int i=0x1; i<=0x100; i=i<<2)
                if(isParamFit(i))
                    return i;
            return ALIGN_LEFT;
        }

        public int getVertParam() {
            for(int i=0x2; i<=0x200; i=i<<2)
                if(isParamFit(i))
                    return i;
            return TO_BOTTOM;
        }

        public int[] getOffset(View anchor, PopupWindow window) {
            int anchWidth=anchor.getWidth();
            int anchHeight=anchor.getHeight();

            int winWidth=window.getWidth();
            int winHeight=window.getHeight();
            View view=window.getContentView();
            if(winWidth<=0)
                winWidth=view.getWidth();
            if(winHeight<=0)
                winHeight=view.getHeight();

            int xoff=0;
            int yoff=0;

            switch (getHoriParam()) {
                case ALIGN_LEFT:
                    xoff=0; break;
                case ALIGN_RIGHT:
                    xoff=anchWidth-winWidth; break;
                case TO_LEFT:
                    xoff=-winWidth; break;
                case TO_RIGHT:
                    xoff=anchWidth; break;
                case CENTER_HORI:
                    xoff=(anchWidth-winWidth)/2; break;
                default:break;
            }
            switch (getVertParam()) {
                case ALIGN_ABOVE:
                    yoff=-anchHeight; break;
                case ALIGN_BOTTOM:
                    yoff=-winHeight; break;
                case TO_ABOVE:
                    yoff=-anchHeight-winHeight; break;
                case TO_BOTTOM:
                    yoff=0; break;
                case CENTER_VERT:
                    yoff=(-winHeight-anchHeight)/2; break;
                default:break;
            }
            return new int[]{ xoff, yoff };
        }
    }
}
