package com.mvp.lt.airlineview.view;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.mvp.lt.airlineview.utils.DensityUtils;

/**
 * $activityName
 *
 * @author ${LiuTao}
 * @date 2018/6/20/020
 */

public class RangeProgressBar extends View {
    private Context mContext;
    // 背景画笔
    private Paint bgPaint;
    // 初始和跨越角度
    private float startAngle = 135.0f;
    private float sweepAngle = 270.0f;
    // 弧形条的宽度 即bgPaint的宽度
    private float bgThickness = DensityUtils.dip2Pix(getContext(), 13);
    // 弧形条内部的宽度
    protected float fgThickness = DensityUtils.dip2Pix(getContext(), 13);
    // 画弧形的矩阵区域
    private RectF mRadialScoreRect;
    // 圆的直径
    private int mDiameter;
    // 矩形区域坐标
    float right;
    float bottom;
    float left;
    float top;
    // 弧形的半径
    protected float mRadius;
    // 弧形的圆心点
    public Point centerPoint = null;

    // 当前的进度所对应的角度
    public double degree;
    // 当前进度所对应的值
    public int currentValue;
    public int upValue;

    // 屏幕的宽高
    private int width;
    private int height;

    //最大值 最小值
    public int maxValue;
    public int minValue;

    //-----------------------------------------------------
    private int processLeft;
    private int processRight;
    private double curDegreeLeft;
    private double curDegreeRight;
    // 内景进度画笔
    private Paint fgPaintRight;
    private Paint fgPaintLeft;
    //拖动球  画笔
    private Paint mThumbRightPaint;
    private Paint mThumbLeftPaint;
    private int circleWidth = DensityUtils.dip2Pix(getContext(), 13);
    //文字 画笔
    private Paint textPaint;
    private String textValueLeft = "";
    private String textValueRight = "";
    private int textPaintSize = DensityUtils.dip2Pix(getContext(), 12);
    //拖动球 距离 圆弧 距离
    public int THUMB_SPACE = DensityUtils.dip2Pix(getContext(), 22);

    private Point arcPointLeft = null;
    private Point arcPointRight = null;
    // arcPoint坐标
    int arcX = 0;
    int arcY = 0;
    int arcX2 = 0;
    int arcY2 = 0;

    private RectF thumbRectfLeft;
    private RectF thumbRectfRight;

    private boolean isThumbLeftselected = false;
    private boolean isThumbRightselected = false;
    private boolean isMoved = false;

    public Point getArcPoint() {
        return arcPointLeft;
    }

    public void setCurDegreeLeft(double curDegree) {
        this.curDegreeLeft = curDegree;
    }

    public void setCurDegreeRight(double curDegree2) {
        this.curDegreeRight = curDegree2;
    }

    public void setThumbRectfHeat(Point arcPoint) {
        thumbRectfLeft = new RectF(arcPoint.x-circleWidth, arcPoint.y-circleWidth,
                arcPoint.x+circleWidth, arcPoint.y+circleWidth);
    }

    public void setThumbRectfCool(Point arcPoint) {
        thumbRectfRight = new RectF(arcPoint.x-circleWidth, arcPoint.y-circleWidth,
                arcPoint.x+circleWidth, arcPoint.y+circleWidth);
    }


    public int getMaxValue() {
        return maxValue;
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getProcessLeft() {
        return processLeft;
    }

    public int getProcessRight() {
        return processRight;
    }

    public int getUpValue() {
        return upValue;
    }

    public void setUpValue(int upValue) {
        this.upValue = upValue;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }

    public double getDegree() {
        return degree;
    }

    public void setDegree(double degree) {
        this.degree = degree;
    }


    /**
     * 提供给外部访问值的接口
     */
    private OnAutoMoveViewValueChanged mMove;
    private OnAutoUpViewValueChanged mUp;

    public interface OnAutoMoveViewValueChanged {
        public void onMoveChanged(String value1, String value2);
    }

    public interface OnDownViewValueChanged {
        public void onDownChanged(int value);
    }

    public interface OnAutoUpViewValueChanged {
        public void onUpChanged(String value1, String value2);
    }

    public void setOnAutoMoveViewValueChanged(OnAutoMoveViewValueChanged move) {
        mMove = move;
    }

    public void setOnAutoUpViewValueChanged(OnAutoUpViewValueChanged up) {
        mUp = up;
    }

    public RangeProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    public RangeProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public RangeProgressBar(Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void init() {
        // 背景Paint
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(bgThickness);
        bgPaint.setColor(Color.parseColor("#E5E5E5"));
        BlurMaskFilter blurMaskFilter = new BlurMaskFilter(1, BlurMaskFilter.Blur.INNER);
        bgPaint.setMaskFilter(blurMaskFilter);

        mRadialScoreRect = new RectF(0, 0, mDiameter, mDiameter);
        centerPoint = new Point((int) (left + mRadius), (int) (top + mRadius));

        mThumbRightPaint = new Paint();
        mThumbRightPaint.setAntiAlias(true);
        mThumbRightPaint.setColor(Color.parseColor("#3370CC"));
        mThumbRightPaint.setStrokeWidth(1);

        mThumbLeftPaint = new Paint();
        mThumbLeftPaint.setAntiAlias(true);
        mThumbLeftPaint.setColor(Color.parseColor("#ff0000"));
        mThumbLeftPaint.setStrokeWidth(1);

        //渐变色
        int[] colors = {0xFFE5BD7D, 0xFFFAAA64,0xFFFFFFFF, 0xFF6AE2FD,
                0xFF8CD0E5, 0xFFA3CBCB,0xFFBDC7B3, 0xFFD1C299, 0xFFE5BD7D};
        SweepGradient mSweepGradient = new SweepGradient(360, 360, colors, null);
        // 内景Paint
        fgPaintRight = new Paint(Paint.ANTI_ALIAS_FLAG);
        fgPaintRight.setStyle(Paint.Style.STROKE);
        fgPaintRight.setStrokeWidth(fgThickness);
        fgPaintRight.setShader(mSweepGradient);

        fgPaintLeft = new Paint(Paint.ANTI_ALIAS_FLAG);
        fgPaintLeft.setStyle(Paint.Style.STROKE);
        fgPaintLeft.setStrokeWidth(fgThickness);
        fgPaintLeft.setColor(Color.parseColor("#E5E5E5"));

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.parseColor("#FFFFFF"));
        textPaint.setTextSize(textPaintSize);
        textPaint.setTextAlign(Paint.Align.CENTER);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        /**
         * Determine the diameter and the radius based on device orientation
         * 为了与背景图片重合,按比例将圆弧半径缩小
         */
        if (w > h) {
            mDiameter = h;
            mRadius = ((mDiameter / 2 - (getPaddingTop() + getPaddingBottom())) * 7) / 10;
        } else {
            mDiameter = w;
            mRadius = ((mDiameter / 2 - (getPaddingLeft() + getPaddingRight())) * 7) / 10;
        }
        // Init the draw arc Rect object
        left = (getWidth() / 2) - mRadius + getPaddingLeft();
        right = (getWidth() / 2) + mRadius - getPaddingRight();
        top = (getHeight() / 2) - mRadius + getPaddingTop();
        bottom = (getHeight() / 2) + mRadius - getPaddingBottom();
        mRadialScoreRect = new RectF(left, top, right, bottom);

        centerPoint = new Point((int) (left + mRadius), (int) (top + mRadius));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

		/*
		 * startAngle 开始角度 sweepAngle 跨越角度 false 不画中心和弧线的连线 drawArc 绘制弧线
		 */
        canvas.drawArc(mRadialScoreRect, startAngle, sweepAngle, false, bgPaint);

        if(!isMoved){
            setCurDegreeLeft(processLeft* 270 / 100);
            setArcPointLeft(curDegreeLeft);
            setCurDegreeRight(processRight* 270 / 100);
            setArcPointRight(curDegreeRight);
        }

        drawProgress(canvas);
        drawThumb(canvas);
        drawText(canvas);

    }

    private void drawThumb(Canvas canvas){

        if (arcPointLeft != null) {
            canvas.drawCircle(arcPointLeft.x, arcPointLeft.y, circleWidth, mThumbLeftPaint);
        }

        if (arcPointRight != null) {
            canvas.drawCircle(arcPointRight.x, arcPointRight.y, circleWidth, mThumbRightPaint);
        }
    }

    private void drawProgress(Canvas canvas){

        RectF fgRect = new RectF(left, top, right, bottom);

        canvas.drawArc(fgRect, startAngle, (float)curDegreeRight, false, fgPaintRight);

        canvas.drawArc(fgRect, startAngle, (float)curDegreeLeft, false, fgPaintLeft);

    }

    private void drawText(Canvas canvas){

        if (arcPointLeft != null) {
            canvas.drawText(textValueLeft, arcPointLeft.x,arcPointLeft.y+circleWidth/2, textPaint);
        }

        if (arcPointRight != null) {
            canvas.drawText(textValueRight, arcPointRight.x,arcPointRight.y+circleWidth/2, textPaint);
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = 0;
        float y = 0;
        x = event.getX();
        y = event.getY();

        degree = getAngle(x, y, centerPoint);

        if(thumbRectfLeft.contains(x, y)){
            setArcPointLeft(degree);
            setCurDegreeLeft(degree);
        }
        else if(thumbRectfRight.contains(x, y)){
            setArcPointRight(degree);
            setCurDegreeRight(degree);

        }else{
            return true;
        }

        switch (event.getAction()) {
            // 屏幕按下
            case MotionEvent.ACTION_DOWN:
                float x1 = event.getX();
                float y1 = event.getY();
                if(thumbRectfLeft.contains(x1, y1)){
                    isThumbLeftselected = true;
                }
                else{
                    isThumbLeftselected = false;
                }

                if(thumbRectfRight.contains(x, y)){
                    isThumbRightselected = true;
                }
                else{
                    isThumbRightselected = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                isMoved = true;
                setCurrentValue((int) Math.round((degree * 100) / 270));

                setProgressValue();
                if (mMove != null)
                    mMove.onMoveChanged(textValueLeft,textValueRight);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:

                setUpValue((int) Math.round((degree * 100) / 270));
                setCurrentValue((int) Math.round((degree * 100) / 270));
                setProgressValue();

                if (mUp != null){
                    mUp.onUpChanged(textValueLeft,textValueRight);
                }
                isThumbLeftselected = false;
                isThumbRightselected = false;
                invalidate();
                break;
        }

        return true;
    }

    private void setProgressValue(){
        if(isThumbLeftselected){
            textValueLeft = getValueByProgress(getCurrentValue());
            setArcPointLeft(degree);
            setCurDegreeLeft(degree);

            if(curDegreeLeft >= curDegreeRight - 40){
                curDegreeRight = curDegreeLeft + 40;
                if(curDegreeRight > 270.0){
                    curDegreeRight = 270.0;
                    curDegreeLeft = curDegreeRight - 40;
                }
                textValueLeft = getValueByProgress((int) Math.round((curDegreeLeft * 100) / 270));
                textValueRight = getValueByProgress((int) Math.round((curDegreeRight * 100) / 270));
                setArcPointLeft(curDegreeLeft);
                setArcPointRight(curDegreeRight);
            }
        }
        if(isThumbRightselected){
            textValueRight = getValueByProgress(getCurrentValue());
            setArcPointRight(degree);
            setCurDegreeRight(degree);

            if(curDegreeRight <= curDegreeLeft + 40){
                curDegreeLeft = curDegreeRight - 40;
                if(curDegreeLeft < 0.0){
                    curDegreeLeft = 0.0;
                    curDegreeRight = curDegreeLeft + 40;
                }
                textValueLeft = getValueByProgress((int) Math.round((curDegreeLeft * 100) / 270));
                textValueRight = getValueByProgress((int) Math.round((curDegreeRight * 100) / 270));
                setArcPointLeft(curDegreeLeft);
                setArcPointRight(curDegreeRight);
            }
        }
    }

    // 根据中心点计算手指touch的角度
    private double getAngle(Float x, Float y, Point point) {
        float a = x - point.x;
        float b = y - point.y;
        double angle = Math.toDegrees(Math.atan2(b, a)) + 225;
        if (angle > 360) {
            angle = angle - 360;
        }
        if (angle > 270) {
            // 在第三象限
            if (a < 0) {
                angle = 0;
            }
            // 在第二象限
            else {
                angle = 270;
            }
        }
        return angle;
    }

    /**
     * 计算出当前点击位置对应的弧线上的坐标点
     *
     * @param degree
     */

    public void setArcPointLeft(double degree) {
        double radians = Math.toRadians(degree - 45);
        double incrementX = Math.cos(radians) * (mRadius+THUMB_SPACE);
        double incrementY = Math.sin(radians) * (mRadius+THUMB_SPACE);
        arcX = (int) (centerPoint.x - incrementX);
        arcY = (int) (centerPoint.y - incrementY);

        if (mRadius != 0) {
            arcPointLeft = new Point(arcX, arcY);
            thumbRectfLeft = new RectF(arcX-circleWidth-THUMB_SPACE, arcY-circleWidth-THUMB_SPACE,
                    arcX+circleWidth+THUMB_SPACE, arcY+circleWidth+THUMB_SPACE);
        }
    }

    public void setArcPointRight(double degree2) {
        double radians = Math.toRadians(degree2 - 45);
        double incrementX = Math.cos(radians) * (mRadius+THUMB_SPACE);
        double incrementY = Math.sin(radians) * (mRadius+THUMB_SPACE);
        arcX2 = (int) (centerPoint.x - incrementX);
        arcY2 = (int) (centerPoint.y - incrementY);

        if (mRadius != 0) {
            arcPointRight = new Point(arcX2, arcY2);
            thumbRectfRight = new RectF(arcX2-circleWidth-THUMB_SPACE, arcY2-circleWidth-THUMB_SPACE,
                    arcX2+circleWidth+THUMB_SPACE, arcY2+circleWidth+THUMB_SPACE);
        }
    }

    // 提供对应修改角度方法,实现point位置的显示
    public void setProcessLeft(String processValueLeft) {
        textValueLeft = processValueLeft;
        this.processLeft = getProgressByValue(processValueLeft);
        setCurDegreeLeft(processLeft* 270 / 100);
        setArcPointLeft(curDegreeLeft);
        isMoved = false;
        postInvalidate();
    }

    public void setProcessRight(String processValueRight) {
        textValueRight = processValueRight;
        this.processRight = getProgressByValue(processValueRight);
        setCurDegreeRight(processRight* 270 / 100);
        setArcPointRight(curDegreeRight);
        isMoved = false;
        postInvalidate();
    }

    //根据初始值计算进度
    public int getProgressByValue(String value){
        int progressValue;
        float i = Float.parseFloat(value);
        progressValue = (int) Math.round((i - minValue)*100/(maxValue - minValue));
        return progressValue;
    }

    //根据进度计算值
    public String getValueByProgress(int progress){
        String value = "";
        value= progress*(maxValue - minValue)/100+minValue+"";
        return value;
    }

}
