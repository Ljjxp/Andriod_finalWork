package com.example.ljj.finalminidowyinapp.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * 为了实现都要圆形图片效果，这里直接提供给大家使用该控件
 */
public class CircleImageView extends AppCompatImageView {


    private int width;
    private int height;
    private float radius;
    private Xfermode xfermode;
    private Paint mPaint;
    private Path mPath;
    private RectF mRectF;// 用来裁剪图片的path


    public CircleImageView(Context context) {
        this(context, null);
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(15);
        mPath  = new Path();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        //设置外框的矩形区域，不可再init()初始化，构造器中width和height还未确定，可在onMesure()中获取并设置
        mRectF = new RectF(0,0, getWidth(),getHeight());
        //path划出一个圆角矩形，容纳图片,图片矩形区域设置比红色外框小，否则会覆盖住外框，随意控制
        mPath.addRoundRect(new RectF(5, 5, mRectF.right-5,mRectF.bottom-5), 25, 25, Path.Direction.CW);

        canvas.clipPath(mPath);//将canvas裁剪到path设定的区域，往后的绘制都只能在此区域中，

        //这一句应该放在canvas.clipPath(path)之后,canvas.clipPath(path)只对裁剪之后的绘制起作用，
        // 这个方法在ImageView中会画出xml设置的Drawable,落在刚才设置的path中
        super.onDraw(canvas);
    }

    /**
     * 计算图片原始区域的RectF
     */

}
