package com.example.myapplication.view;


import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;


import com.example.myapplication.R;

import java.math.BigDecimal;

/**
 * 双向滑块的进度条（区域选择）
 */
public class SeekRangeBar extends View {
    // 滑块大小
    private static final int imageWidth = 50;
    private static final int imageHeight = 50;

    private Context _context;
    private static final int CLICK_ON_LOW = 1;    //手指在前滑块上滑动
    private static final int CLICK_ON_HIGH = 2;    //手指在后滑块上滑动
    private static final int CLICK_IN_LOW_AREA = 3;  //手指点击离前滑块近
    private static final int CLICK_IN_HIGH_AREA = 4; //手指点击离后滑块近
    private static final int CLICK_OUT_AREA = 5;   //手指点击在view外
    private static final int CLICK_INVAILD = 0;
    private static final int[] STATE_NORMAL = {};
    private static final int[] STATE_PRESSED =
            {android.R.attr.state_pressed,android.R.attr.state_window_focused,};
    private static int mThumbMarginTop = 0;  //滑动块顶部离view顶部的距离
    private static int mTextViewMarginTop = 0;  //当前滑块文字距离view顶部距离
    private Drawable hasScrollBarBg;    //滑动条滑动后背景图
    private Drawable notScrollBarBg;    //滑动条未滑动背景图
    private Drawable mThumbLow;     //前滑块
    private Drawable mThumbHigh;    //后滑块
    private int mScollBarWidth;   //控件宽度 = 滑动条宽度 + 滑动块宽度
    private int mScollBarHeight;  //控件高度
    private int mThumbWidth;    //滑动块直径
    private double mOffsetLow = 0;   //前滑块中心坐标
    private double mOffsetHigh = 0;  //后滑块中心坐标
    private int mDistance=0;   //总刻度是固定距离 两边各去掉半个滑块距离
    private int mFlag = CLICK_INVAILD;  //手指按下的类型
    private double defaultScreenLow = 0;  //默认前滑块位置百分比
    private double defaultScreenHigh = 100; //默认后滑块位置百分比
    private OnSeekBarChangeListener mBarChangeListener;
    private boolean editable=true;//是否处于可编辑状态
    private int miniGap=5;//AB的最小间隔
    private double progressLow;//起点(百分比)
    private double progressHigh;//终点

    public SeekRangeBar(Context context) {
        this(context, null);
    }
    public SeekRangeBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public SeekRangeBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        _context=context;
        //这里设置背景图及滑块图，自定义过进度条的同学应该很熟悉了
        notScrollBarBg = ContextCompat.getDrawable(_context, R.mipmap.grey_color);
        hasScrollBarBg = ContextCompat.getDrawable(_context, R.mipmap.theme_color);

        //左右滑块
        Bitmap bitmapLow = getBitmap(_context, R.drawable.ic_baseline_panorama_fish_eye_24);
        int bitmapHeight = bitmapLow.getHeight();
        int bitmapWidth = bitmapLow.getWidth();
        // 设置想要的大小
        int newWidth = imageWidth;
        int newHeight = imageHeight;
        // 计算缩放比例
        Log.i(TAG, "init: " + String.valueOf(bitmapWidth) + " / " + String.valueOf(newWidth));
        float scaleWidth = ((float) newWidth) / bitmapWidth;
        float scaleHeight = ((float) newHeight) / bitmapHeight;
        Matrix matrix = new Matrix();
        matrix.postScale((float)scaleWidth, (float)scaleHeight);

        bitmapLow = Bitmap.createBitmap(bitmapLow, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
        mThumbLow =  bitmapToDrawable(bitmapLow);
        mThumbHigh = mThumbLow;



        mThumbLow.setState(STATE_NORMAL);
        mThumbHigh.setState(STATE_NORMAL);
        //设置滑动条高度
        mScollBarHeight = notScrollBarBg.getIntrinsicHeight();
        //设置滑动块直径
        mThumbWidth = mThumbLow.getIntrinsicWidth();
    }

    /**
     * 测量view尺寸（在onDraw()之前）
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        mScollBarWidth = width;
        if(mDistance==0) {//这里滑块中心坐标初始化的时候测量一下（根据mDistance是否赋值判断），并不需要不停地去测量。后面会根据进度计算滑块位置。
            mOffsetLow = mThumbWidth / 2;
            mOffsetHigh = width - mThumbWidth / 2;
        }
        mDistance = width - mThumbWidth;
        if(defaultScreenLow != 0) {
            mOffsetLow = formatInt(defaultScreenLow / 100 * (mDistance)) + mThumbWidth / 2;
        }
        if(defaultScreenHigh != 100) {
            mOffsetHigh = formatInt(defaultScreenHigh / 100 * (mDistance)) + mThumbWidth / 2;
        }
        setMeasuredDimension(width, mThumbWidth + mThumbMarginTop + 2);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    /**
     * 绘制进度条
     */
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //设置绘制样式
        Paint text_Paint = new Paint();
        text_Paint.setTextAlign(Paint.Align.CENTER);
        text_Paint.setColor(Color.RED);
        text_Paint.setTextSize(20);

        int top = mThumbMarginTop + mThumbWidth / 2 - mScollBarHeight / 2;
        int bottom = top + mScollBarHeight;

        //绘制是否可操作状态的下的不同样式，仅可编辑状态下显示进度条
        if(editable) {
            //白色滑动条，两个滑块各两边部分
            notScrollBarBg.setBounds(mThumbWidth / 2, top, mScollBarWidth - mThumbWidth / 2, bottom);
            notScrollBarBg.draw(canvas);

            //红色滑动条，两个滑块中间部分
            hasScrollBarBg.setBounds((int) mOffsetLow, top, (int) mOffsetHigh, bottom);
            hasScrollBarBg.draw(canvas);
        }

        //前滑块
        mThumbLow.setBounds((int) (mOffsetLow - mThumbWidth / 2), mThumbMarginTop, (int) (mOffsetLow + mThumbWidth / 2), mThumbWidth + mThumbMarginTop);
        mThumbLow.draw(canvas);

        //后滑块
        mThumbHigh.setBounds((int) (mOffsetHigh - mThumbWidth / 2), mThumbMarginTop, (int) (mOffsetHigh + mThumbWidth / 2), mThumbWidth + mThumbMarginTop);
        mThumbHigh.draw(canvas);

        //当前滑块刻度
        progressLow = formatInt((mOffsetLow - mThumbWidth / 2) * 100 / mDistance);
        progressHigh = formatInt((mOffsetHigh - mThumbWidth / 2) * 100 / mDistance);
        canvas.drawText((int) progressLow + "", (int) mOffsetLow - 2 - 2, mTextViewMarginTop, text_Paint);
        canvas.drawText((int) progressHigh + "", (int) mOffsetHigh - 2, mTextViewMarginTop, text_Paint);

        if (mBarChangeListener != null) {
            mBarChangeListener.onProgressChanged(this, progressLow, progressHigh);
        }
    }

    //手势监听
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if(!editable) {
            return false;
        }
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            mFlag = getAreaFlag(e);
            if (mFlag == CLICK_ON_LOW) {
                mThumbLow.setState(STATE_PRESSED);
            } else if (mFlag == CLICK_ON_HIGH) {
                mThumbHigh.setState(STATE_PRESSED);
            } else if (mFlag == CLICK_IN_LOW_AREA) {
                mThumbLow.setState(STATE_PRESSED);
                mThumbHigh.setState(STATE_NORMAL);
                //如果点击0-mThumbWidth/2坐标
                if (e.getX() < 0 || e.getX() <= mThumbWidth / 2) {
                    mOffsetLow = mThumbWidth / 2;
                } else if (e.getX() > mScollBarWidth - mThumbWidth / 2) {
                    mOffsetLow = mThumbWidth / 2 + mDistance;
                } else {
                    mOffsetLow = formatInt(e.getX());

                }
            } else if (mFlag == CLICK_IN_HIGH_AREA) {
                mThumbHigh.setState(STATE_PRESSED);
                mThumbLow.setState(STATE_NORMAL);
                if (e.getX() >= mScollBarWidth - mThumbWidth / 2) {
                    mOffsetHigh = mDistance + mThumbWidth / 2;
                } else {
                    mOffsetHigh = formatInt(e.getX());
                }
            }
            //更新滑块
            invalidate();
        } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
            if (mFlag == CLICK_ON_LOW) {
                if (e.getX() < 0 || e.getX() <= mThumbWidth / 2) {
                    mOffsetLow = mThumbWidth / 2;
                } else if (e.getX() >= mScollBarWidth - mThumbWidth / 2) {
                    mOffsetLow = mThumbWidth / 2 + mDistance;
                    mOffsetHigh = mOffsetLow;
                } else {
                    mOffsetLow = formatInt(e.getX());
                    if (mOffsetHigh - mOffsetLow <= 0) {
                        mOffsetHigh = (mOffsetLow <= mDistance + mThumbWidth / 2) ? (mOffsetLow) : (mDistance + mThumbWidth / 2);
                    }
                }
            } else if (mFlag == CLICK_ON_HIGH) {
                if (e.getX() < mThumbWidth / 2) {
                    mOffsetHigh = mThumbWidth / 2;
                    mOffsetLow = mThumbWidth / 2;
                } else if (e.getX() > mScollBarWidth - mThumbWidth / 2) {
                    mOffsetHigh = mThumbWidth / 2 + mDistance;
                } else {
                    mOffsetHigh = formatInt(e.getX());
                    if (mOffsetHigh - mOffsetLow <= 0) {
                        mOffsetLow = (mOffsetHigh >= mThumbWidth / 2) ? (mOffsetHigh) : mThumbWidth / 2;
                    }
                }
            }
            //更新滑块，每次滑块有动作都要执行此函数触发onDraw方法绘制新图片
            invalidate();
        } else if (e.getAction() == MotionEvent.ACTION_UP) {
            Log.d("LOGCAT","ACTION UP:"+progressHigh+"-"+progressLow);
            mThumbLow.setState(STATE_NORMAL);
            mThumbHigh.setState(STATE_NORMAL);
            if(miniGap>0 && progressHigh<progressLow+miniGap){
                progressHigh=progressLow+miniGap;
                this.defaultScreenHigh = progressHigh;
                mOffsetHigh = formatInt(progressHigh / 100 * (mDistance)) + mThumbWidth / 2;
                invalidate();
            }
        }
        return true;
    }

    /**
     * 设置是否可编辑状态，非可编辑状态将不能对AB点进行操作
     * @param _b
     */
    public void setEditable(boolean _b){
        editable=_b;
        invalidate();
    }

    /**
     * 获取当前手指位置
     */
    public int getAreaFlag(MotionEvent e) {
        int top = mThumbMarginTop;
        int bottom = mThumbWidth + mThumbMarginTop;
        if (e.getY() >= top && e.getY() <= bottom && e.getX() >= (mOffsetLow - mThumbWidth / 2) && e.getX() <= mOffsetLow + mThumbWidth / 2) {
            return CLICK_ON_LOW;
        } else if (e.getY() >= top && e.getY() <= bottom && e.getX() >= (mOffsetHigh - mThumbWidth / 2) && e.getX() <= (mOffsetHigh + mThumbWidth / 2)) {
            return CLICK_ON_HIGH;
        } else if (e.getY() >= top
                && e.getY() <= bottom
                && ((e.getX() >= 0 && e.getX() < (mOffsetLow - mThumbWidth / 2)) || ((e.getX() > (mOffsetLow + mThumbWidth / 2))
                && e.getX() <= ((double) mOffsetHigh + mOffsetLow) / 2))) {
            return CLICK_IN_LOW_AREA;
        } else if (e.getY() >= top && e.getY() <= bottom && (((e.getX() > ((double) mOffsetHigh + mOffsetLow) / 2) && e.getX() < (mOffsetHigh - mThumbWidth / 2)) || (e.getX() > (mOffsetHigh + mThumbWidth / 2) && e.getX() <= mScollBarWidth))) {
            return CLICK_IN_HIGH_AREA;
        } else if (!(e.getX() >= 0 && e.getX() <= mScollBarWidth && e.getY() >= top && e.getY() <= bottom)) {
            return CLICK_OUT_AREA;
        } else {
            return CLICK_INVAILD;
        }
    }

    /**
     * 设置前滑块位置
     * @param progressLow
     */
    public void setProgressLow(double progressLow) {
        this.defaultScreenLow = progressLow;
        mOffsetLow = formatInt(progressLow / 100 * (mDistance)) + mThumbWidth / 2;
        invalidate();
    }

    /**
     * 设置后滑块位置
     * @param progressHigh
     */
    public void setProgressHigh(double progressHigh) {
        this.defaultScreenHigh = progressHigh;
        mOffsetHigh = formatInt(progressHigh / 100 * (mDistance)) + mThumbWidth / 2;
        invalidate();
    }

    /**
     * 设置滑动监听
     * @param mListener
     */
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener mListener) {
        this.mBarChangeListener = mListener;
    }

    /**
     * 滑动监听，改变输入框的值
     */
    public interface OnSeekBarChangeListener {
        //滑动时
        public void onProgressChanged(SeekRangeBar seekBar, double progressLow, double progressHigh);
    }

    /**
     * 设置滑动结果为整数
     */
    private int formatInt(double value) {
        BigDecimal bd = new BigDecimal(value);
        BigDecimal bd1 = bd.setScale(0, BigDecimal.ROUND_HALF_UP);
        return bd1.intValue();
    }

    private static Bitmap getBitmap(Context context, int vectorDrawableId) {
        Bitmap bitmap = null;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Drawable vectorDrawable = context.getDrawable(vectorDrawableId);
            bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                    vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
        } else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), vectorDrawableId);
        }
        return bitmap;
    }

    public static Drawable bitmapToDrawable(Bitmap bitmap) {
        return new BitmapDrawable(Resources.getSystem(), bitmap);
    }
}

