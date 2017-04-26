package com.lq.zcoder.morethreaddownload.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.icu.text.DecimalFormat;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.lq.zcoder.morethreaddownload.R;

/**
 * Created by Zcoder
 * Email : 1340751953@qq.com
 * Time :  2017/4/25
 * Description :
 */
public class CircleProgressbar extends View {
    private final String TAG = "CircleProgressbar";
    //显示风格
    private int mShowStyle;
    //完成的颜色
    private int mDoneColor;
    //未完成的颜色
    private int mUndoneColor;
    //进度文字的颜色
    private int mTextColor;
    //显示进度文字
    private boolean mShowText;
    //最大进度值
    private int mMaxProgress;
    //圆环的宽度
    private int mRingWidth;


    //***********default**************
    private final int SHOW_STYLE = 0;
    private final int DONE_COLOR = Color.parseColor("#f38181");
    private final int UNDONE_COLOR = Color.parseColor("#eeeeee");
    private final boolean SHOW_TEXT = false;
    private final int TEXT_COLOR = Color.parseColor("#f38181");
    private final int MAX_PROGRESS = 100;
    private final int RING_WIDTH = 10;


    private int mProgress = 50;
    private Paint mTextPaint;
    private Paint mUndonePaint;
    private Paint mDonePaint;
    private int mCenter_point;
    private int mExcircleRadius;
    private RectF mRectF;

    public CircleProgressbar(Context context) {
        this(context, null);
    }

    public CircleProgressbar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        initPaint();
    }

    private void initPaint() {
        mTextPaint = new Paint();
        mTextPaint.setColor(TEXT_COLOR);
        mTextPaint.setAntiAlias(true);

        mUndonePaint = new Paint();
        mUndonePaint.setAntiAlias(true);
        mUndonePaint.setColor(UNDONE_COLOR);

        mDonePaint = new Paint();
        mDonePaint.setAntiAlias(true);
        mDonePaint.setColor(DONE_COLOR);
    }

    //初始化属性
    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.circle_progress_bar);
        mShowStyle = typedArray.getInt(R.styleable.circle_progress_bar_show_style, SHOW_STYLE);
        mShowText = typedArray.getBoolean(R.styleable.circle_progress_bar_show_text, SHOW_TEXT);
        mTextColor = typedArray.getColor(R.styleable.circle_progress_bar_text_color, TEXT_COLOR);
        mDoneColor = typedArray.getColor(R.styleable.circle_progress_bar_color_done, DONE_COLOR);
        mUndoneColor = typedArray.getColor(R.styleable.circle_progress_bar_color_undone, UNDONE_COLOR);
        mMaxProgress = typedArray.getInt(R.styleable.circle_progress_bar_max_progress, MAX_PROGRESS);
        mRingWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                typedArray.getInt(R.styleable.circle_progress_bar_ring_width, RING_WIDTH),
                context.getResources().getDisplayMetrics());
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);

        String progressText = new DecimalFormat("#.##%").format((float) mProgress / mMaxProgress);
        if (progressText.contains("100")) {
            progressText = "完成";
        }
        int textHeight = getHeight() / 4;
        mTextPaint.setTextSize(textHeight);
        mTextPaint.setStyle(Paint.Style.FILL);
        float textwidth = mTextPaint.measureText(progressText);
        float x = getWidth() / 2 - textwidth / 2;
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float dy = -(fontMetrics.descent + fontMetrics.ascent) / 2;
        float y = getHeight() / 2 + dy;
        canvas.drawText(progressText, x, y, mTextPaint);
        //画最大的圆
        //获取圆心
        mCenter_point = getWidth() / 2;
        switch (mShowStyle) {
            case 0:
                //环形进度条绘制相关
                drawRing(canvas);
                break;
            case 1:
                drawSector(canvas);
                break;
        }
    }

    private void drawSector(Canvas canvas) {
        //外圆半径
        mExcircleRadius = getWidth() / 2;
        canvas.drawCircle(mCenter_point, mCenter_point, mExcircleRadius, mUndonePaint);
        mDonePaint.setStyle(Paint.Style.FILL);
        canvas.drawArc(new RectF(0, 0, getWidth(), getWidth()), -90, (float) mProgress / mMaxProgress * 360, true, mDonePaint);
    }

    private void drawRing(Canvas canvas) {
        //外圆半径
        mExcircleRadius = getWidth() / 2 - mRingWidth / 2;
        mUndonePaint.setStyle(Paint.Style.STROKE);
        mUndonePaint.setStrokeWidth(mRingWidth);
        canvas.drawCircle(mCenter_point, mCenter_point, mExcircleRadius, mUndonePaint);
        mDonePaint.setStyle(Paint.Style.STROKE);
        mDonePaint.setStrokeWidth(mRingWidth);
        canvas.drawArc(new RectF(mRingWidth / 2, mRingWidth / 2, getWidth() - mRingWidth / 2, getWidth() - mRingWidth / 2), -90, (float) mProgress / mMaxProgress * 360, false, mDonePaint);
    }

    public void setProgress(int progress) {
        mProgress = progress;
        invalidate();
    }

    public void setMaxProgress(int maxProgress) {
        mMaxProgress = maxProgress;
    }

    public int getmShowStyle() {
        return mShowStyle;
    }

    public void setmShowStyle(int mShowStyle) {
        this.mShowStyle = mShowStyle;
    }

    public int getmDoneColor() {
        return mDoneColor;
    }

    public void setmDoneColor(int mDoneColor) {
        this.mDoneColor = mDoneColor;
    }

    public int getmUndoneColor() {
        return mUndoneColor;
    }

    public void setmUndoneColor(int mUndoneColor) {
        this.mUndoneColor = mUndoneColor;
    }

    public int getmTextColor() {
        return mTextColor;
    }

    public void setmTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }

    public boolean ismShowText() {
        return mShowText;
    }

    public void setmShowText(boolean mShowText) {
        this.mShowText = mShowText;
    }
}
