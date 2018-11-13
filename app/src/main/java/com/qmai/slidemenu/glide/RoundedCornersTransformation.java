package com.qmai.slidemenu.glide;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;

/**
 * 根据https://github.com/wasabeef/glide-transformations修改
 * Author:caiyoufei
 * Date:2016/8/1
 * Time:12:02
 */
public class RoundedCornersTransformation implements Transformation<Bitmap> {
    private final String TAG = "GlidesTransformation";

    private static final int RADIUS = 5;

    public enum CornerType {
        ALL,
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT,
        TOP, BOTTOM, LEFT, RIGHT,
        OTHER_TOP_LEFT, OTHER_TOP_RIGHT, OTHER_BOTTOM_LEFT, OTHER_BOTTOM_RIGHT,
        DIAGONAL_FROM_TOP_LEFT, DIAGONAL_FROM_TOP_RIGHT
    }

    private BitmapPool mBitmapPool;
    private float mRadius;
    private float mDiameter;
    private float mMargin;
    private CornerType mCornerType;
    /**
     * 是否为圆形图
     */
    private boolean isCircle;
    /**
     * 下载的图片压缩后的最大(KB)
     */
    private int maxSizeImage = 100;

    /**
     * 默认四周10dp弧角，没有margin边距
     *
     * @param context 上下文
     */
    public RoundedCornersTransformation(Context context) {
        this(context, RADIUS, 0, CornerType.ALL);
    }

    public RoundedCornersTransformation(Context context, CornerType cornerType) {
        this(context, RADIUS, 0, cornerType);
    }

    public RoundedCornersTransformation(Context context, int imageMaxSizeKB) {
        this(context, RADIUS, 0, CornerType.ALL);
        this.maxSizeImage = imageMaxSizeKB;
    }

    public RoundedCornersTransformation(Context context, int radiusDP, int marginDP, int imageMaxSizeKB) {
        this(context, radiusDP, marginDP, CornerType.ALL);
        this.maxSizeImage = imageMaxSizeKB;
    }

    /**
     * 圆形构造方法（使用此构造方法请确保控件为正方形）
     *
     * @param context  上下文
     * @param isCircle false为默认10dp的弧角效果,true则执行圆形效果
     */
    public RoundedCornersTransformation(Context context, boolean isCircle) {
        this(context, RADIUS, 0, CornerType.ALL);
        this.isCircle = isCircle;
    }

    public RoundedCornersTransformation(Context context, boolean isCircle, int imageMaxSizeKB) {
        this(context, RADIUS, 0, CornerType.ALL);
        this.isCircle = isCircle;
        this.maxSizeImage = imageMaxSizeKB;
    }

    public RoundedCornersTransformation(Context context, int radiusDP, int marginDP) {
        this(context, radiusDP, marginDP, CornerType.ALL);
    }

    public RoundedCornersTransformation(BitmapPool pool, int radiusDP, int marginDP) {
        this(pool, radiusDP, marginDP, CornerType.ALL);
    }

    public RoundedCornersTransformation(Context context, int radiusDP, int marginDP, CornerType cornerType) {
        this(Glide.get(context).getBitmapPool(), radiusDP, marginDP, cornerType);
    }

    public RoundedCornersTransformation(BitmapPool pool, int radiusDP, int marginDP, CornerType cornerType) {
        mBitmapPool = pool;
        float density = Resources.getSystem().getDisplayMetrics().density;
        mRadius = density * radiusDP;
        mDiameter = mRadius * 2;
        mMargin = density * marginDP;
        mCornerType = cornerType;
    }

    @Override
    public Resource<Bitmap> transform(Context context, Resource<Bitmap> resource, int outWidth, int outHeight) {
        //防止操作图片出错
        try {
            Bitmap source = resource.get();
            //获取原图的宽高
            int width = source.getWidth();
            int height = source.getHeight();
            //需要操作的图片
            Bitmap bitmap;
            //获取缩放比
            float scaleX = 1.0f * outWidth / width;
            float scaleY = 1.0f * outHeight / height;
            //得到图片需要的缩放比
            float scale = Math.max(scaleX, scaleY);
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            //获取到缩放后的图片
            bitmap = Bitmap.createBitmap(source, 0, 0, width, height, matrix, true);
            //获取到缩放后图片的宽高
            int scaleWidth = (int) (width * scale);
            int scaleHeight = (int) (height * scale);
            //获取图片裁剪的开始位置
            int startX;
            int startY;
            //原图太高
            if (height * 1.0f / width > outHeight * 1.0f / outWidth) {
                startX = 0;
                startY = (scaleHeight - outHeight) / 2;
            } else {
                startY = 0;
                startX = (scaleWidth - outWidth) / 2;
            }
            //裁剪得到需要大小的图片
            bitmap = Bitmap.createBitmap(bitmap, startX, startY, outWidth, outHeight);

            //质量压缩，防止图片过大
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 100;
            while (baos.toByteArray().length / 1024 > maxSizeImage && options - 10 > 0) {
                //循环判断如果压缩后图片是否大于maxSizeImage,大于继续压缩
                baos.reset();// 重置baos即清空baos
                options -= 10;// 每次都减少10%
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            }
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//
            // 把压缩后的数据baos存放到ByteArrayInputStream中
            bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
            //不需要圆角和圆形则直接返回压缩后的图片
            if (!isCircle && mRadius == 0 && mMargin == 0) {
                return BitmapResource.obtain(bitmap, mBitmapPool);
            }
            //建立需要画圆角的图片
            Bitmap result = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
            //因为圆角和圆形边缘都会透明，所以需要用含透明度的值(ARGB_8888或ARGB_4444)
            //在图片上进行绘制
            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            if (isCircle) {
                //需要绘制的半径
                float radius = outWidth / 2f;
                canvas.drawCircle(radius, radius, radius, paint);
            } else {
                drawRoundRect(canvas, paint, outWidth, outHeight);
            }
            return BitmapResource.obtain(result, mBitmapPool);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(key().getBytes());
    }

    private void drawRoundRect(Canvas canvas, Paint paint, float width, float height) {
        float right = width - mMargin;
        float bottom = height - mMargin;

        switch (mCornerType) {
            case ALL:
                canvas.drawRoundRect(new RectF(mMargin, mMargin, right, bottom), mRadius, mRadius, paint);
                break;
            case TOP_LEFT:
                drawTopLeftRoundRect(canvas, paint, right, bottom);
                break;
            case TOP_RIGHT:
                drawTopRightRoundRect(canvas, paint, right, bottom);
                break;
            case BOTTOM_LEFT:
                drawBottomLeftRoundRect(canvas, paint, right, bottom);
                break;
            case BOTTOM_RIGHT:
                drawBottomRightRoundRect(canvas, paint, right, bottom);
                break;
            case TOP:
                drawTopRoundRect(canvas, paint, right, bottom);
                break;
            case BOTTOM:
                drawBottomRoundRect(canvas, paint, right, bottom);
                break;
            case LEFT:
                drawLeftRoundRect(canvas, paint, right, bottom);
                break;
            case RIGHT:
                drawRightRoundRect(canvas, paint, right, bottom);
                break;
            case OTHER_TOP_LEFT:
                drawOtherTopLeftRoundRect(canvas, paint, right, bottom);
                break;
            case OTHER_TOP_RIGHT:
                drawOtherTopRightRoundRect(canvas, paint, right, bottom);
                break;
            case OTHER_BOTTOM_LEFT:
                drawOtherBottomLeftRoundRect(canvas, paint, right, bottom);
                break;
            case OTHER_BOTTOM_RIGHT:
                drawOtherBottomRightRoundRect(canvas, paint, right, bottom);
                break;
            case DIAGONAL_FROM_TOP_LEFT:
                drawDiagonalFromTopLeftRoundRect(canvas, paint, right, bottom);
                break;
            case DIAGONAL_FROM_TOP_RIGHT:
                drawDiagonalFromTopRightRoundRect(canvas, paint, right, bottom);
                break;
            default:
                canvas.drawRoundRect(new RectF(mMargin, mMargin, right, bottom), mRadius, mRadius, paint);
                break;
        }
    }

    private void drawTopLeftRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(mMargin, mMargin, mMargin + mDiameter, mMargin + mDiameter),
                mRadius, mRadius, paint);
        canvas.drawRect(new RectF(mMargin, mMargin + mRadius, mMargin + mRadius, bottom), paint);
        canvas.drawRect(new RectF(mMargin + mRadius, mMargin, right, bottom), paint);
    }

    private void drawTopRightRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(right - mDiameter, mMargin, right, mMargin + mDiameter), mRadius,
                mRadius, paint);
        canvas.drawRect(new RectF(mMargin, mMargin, right - mRadius, bottom), paint);
        canvas.drawRect(new RectF(right - mRadius, mMargin + mRadius, right, bottom), paint);
    }

    private void drawBottomLeftRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(mMargin, bottom - mDiameter, mMargin + mDiameter, bottom),
                mRadius, mRadius, paint);
        canvas.drawRect(new RectF(mMargin, mMargin, mMargin + mDiameter, bottom - mRadius), paint);
        canvas.drawRect(new RectF(mMargin + mRadius, mMargin, right, bottom), paint);
    }

    private void drawBottomRightRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(right - mDiameter, bottom - mDiameter, right, bottom), mRadius,
                mRadius, paint);
        canvas.drawRect(new RectF(mMargin, mMargin, right - mRadius, bottom), paint);
        canvas.drawRect(new RectF(right - mRadius, mMargin, right, bottom - mRadius), paint);
    }

    private void drawTopRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(mMargin, mMargin, right, mMargin + mDiameter), mRadius, mRadius,
                paint);
        canvas.drawRect(new RectF(mMargin, mMargin + mRadius, right, bottom), paint);
    }

    private void drawBottomRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(mMargin, bottom - mDiameter, right, bottom), mRadius, mRadius,
                paint);
        canvas.drawRect(new RectF(mMargin, mMargin, right, bottom - mRadius), paint);
    }

    private void drawLeftRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(mMargin, mMargin, mMargin + mDiameter, bottom), mRadius, mRadius,
                paint);
        canvas.drawRect(new RectF(mMargin + mRadius, mMargin, right, bottom), paint);
    }

    private void drawRightRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(right - mDiameter, mMargin, right, bottom), mRadius, mRadius,
                paint);
        canvas.drawRect(new RectF(mMargin, mMargin, right - mRadius, bottom), paint);
    }

    private void drawOtherTopLeftRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(mMargin, bottom - mDiameter, right, bottom), mRadius, mRadius,
                paint);
        canvas.drawRoundRect(new RectF(right - mDiameter, mMargin, right, bottom), mRadius, mRadius,
                paint);
        canvas.drawRect(new RectF(mMargin, mMargin, right - mRadius, bottom - mRadius), paint);
    }

    private void drawOtherTopRightRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(mMargin, mMargin, mMargin + mDiameter, bottom), mRadius, mRadius,
                paint);
        canvas.drawRoundRect(new RectF(mMargin, bottom - mDiameter, right, bottom), mRadius, mRadius,
                paint);
        canvas.drawRect(new RectF(mMargin + mRadius, mMargin, right, bottom - mRadius), paint);
    }

    private void drawOtherBottomLeftRoundRect(Canvas canvas, Paint paint, float right, float bottom) {
        canvas.drawRoundRect(new RectF(mMargin, mMargin, right, mMargin + mDiameter), mRadius, mRadius,
                paint);
        canvas.drawRoundRect(new RectF(right - mDiameter, mMargin, right, bottom), mRadius, mRadius,
                paint);
        canvas.drawRect(new RectF(mMargin, mMargin + mRadius, right - mRadius, bottom), paint);
    }

    private void drawOtherBottomRightRoundRect(Canvas canvas, Paint paint, float right,
                                               float bottom) {
        canvas.drawRoundRect(new RectF(mMargin, mMargin, right, mMargin + mDiameter), mRadius, mRadius,
                paint);
        canvas.drawRoundRect(new RectF(mMargin, mMargin, mMargin + mDiameter, bottom), mRadius, mRadius,
                paint);
        canvas.drawRect(new RectF(mMargin + mRadius, mMargin + mRadius, right, bottom), paint);
    }

    private void drawDiagonalFromTopLeftRoundRect(Canvas canvas, Paint paint, float right,
                                                  float bottom) {
        canvas.drawRoundRect(new RectF(mMargin, mMargin, mMargin + mDiameter, mMargin + mDiameter),
                mRadius, mRadius, paint);
        canvas.drawRoundRect(new RectF(right - mDiameter, bottom - mDiameter, right, bottom), mRadius,
                mRadius, paint);
        canvas.drawRect(new RectF(mMargin, mMargin + mRadius, right - mDiameter, bottom), paint);
        canvas.drawRect(new RectF(mMargin + mDiameter, mMargin, right, bottom - mRadius), paint);
    }

    private void drawDiagonalFromTopRightRoundRect(Canvas canvas, Paint paint, float right,
                                                   float bottom) {
        canvas.drawRoundRect(new RectF(right - mDiameter, mMargin, right, mMargin + mDiameter), mRadius,
                mRadius, paint);
        canvas.drawRoundRect(new RectF(mMargin, bottom - mDiameter, mMargin + mDiameter, bottom),
                mRadius, mRadius, paint);
        canvas.drawRect(new RectF(mMargin, mMargin, right - mRadius, bottom - mRadius), paint);
        canvas.drawRect(new RectF(mMargin + mRadius, mMargin + mRadius, right, bottom), paint);
    }

    public String key() {
        return "RoundedTransformation(radius=" + mRadius + ", margin=" + mMargin + ", diameter="
                + mDiameter + ", cornerType=" + mCornerType.name() + ")";
    }
}