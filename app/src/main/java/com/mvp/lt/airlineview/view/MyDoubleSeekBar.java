package com.mvp.lt.airlineview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.mvp.lt.airlineview.R;
import com.mvp.lt.airlineview.utils.DensityUtils;

import java.math.BigDecimal;

/**
 * 双游标自定义seekbar
 * 优点：
 * 1）不会与其他滑动事件冲突，可用于类似侧滑菜单的布局中
 * 2）自定义游标，带textView跟随滑动
 */
public class MyDoubleSeekBar<T extends Number> extends android.support.v7.widget.AppCompatImageView {
    private boolean fromUser = false;
    private static String TAG = MyDoubleSeekBar.class.getSimpleName();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint thumbValuePaint = getThumbValuePaint();
    private Bitmap thumbImage = BitmapFactory.decodeResource(getResources(), R.drawable.thumb_normal);
    private Bitmap thumbPressedImage = BitmapFactory.decodeResource(getResources(), R.drawable.thumb_hover);

    private float thumbWidth = thumbImage.getWidth();
    private float thumbHalfWidth = 0.5f * thumbWidth;
    private float thumbHalfHeight = 0.5f * thumbImage.getHeight();
    private float padding = thumbHalfWidth;
    private T absoluteMinValue;
    private T absoluteMaxValue;
    private NumberType numberType;
    private double absoluteMinValuePrim, absoluteMaxValuePrim;
    private double normalizedMinValue = 0d;
    private double normalizedMaxValue = 1d;
    private Thumb pressedThumb = null;
    private boolean notifyWhileDragging = false;
    private OnRangeSeekBarChangeListener<T> listener;
    private boolean isEnableDragging = true;

    public static final int DEFAULT_COLOR = Color.argb(0xFF, 0x33, 0xB5, 0xE5);

    public static final int INVALID_POINTER_ID = 255;

    public static final int ACTION_POINTER_UP = 0x6, ACTION_POINTER_INDEX_MASK = 0x0000ff00,
            ACTION_POINTER_INDEX_SHIFT = 8;

    private float mDownMotionX;// ???touchEvent???????X????
    private int mActivePointerId = INVALID_POINTER_ID;
    float mTouchProgressOffset;
    private int mScaledTouchSlop;
    private boolean mIsDragging;
    private int mMY;
    private String mMaxtext = "0";
    private String mMintext = "100";
    /**
     * 是否在执行
     */
    private boolean isExcute = false;


    public void setEnableDragging(boolean enableDragging) {
        isEnableDragging = enableDragging;
    }

    /**
     * @param absoluteMinValue 最小选择的范围
     * @param absoluteMaxValue 最大选择的范围
     * @param context
     * @throws IllegalArgumentException
     */
    public MyDoubleSeekBar(T absoluteMinValue, T absoluteMaxValue, Context context) throws IllegalArgumentException {
        super(context);
        this.absoluteMinValue = absoluteMinValue;
        this.absoluteMaxValue = absoluteMaxValue;
        absoluteMinValuePrim = absoluteMinValue.doubleValue();
        absoluteMaxValuePrim = absoluteMaxValue.doubleValue();
        numberType = NumberType.fromNumber(absoluteMinValue);
        setFocusable(true);
        setFocusableInTouchMode(true);
        init();
    }

    public void updateMaxDate(T absoluteMinValuew, T absoluteMaxValuew) {
        this.absoluteMinValue = absoluteMinValuew;
        this.absoluteMaxValue = absoluteMaxValuew;
        absoluteMinValuePrim = absoluteMinValue.doubleValue();
        absoluteMaxValuePrim = absoluteMaxValue.doubleValue();
        numberType = NumberType.fromNumber(absoluteMinValue);
//        setSelectedMinValue(absoluteMinValuew);
//        setSelectedMaxValue(absoluteMaxValuew);
        init();
        invalidate();
    }

    public void setExcute(boolean excute) {
        isExcute = excute;
    }

    public void updateSelectedValue(T normalizedMinValue, T normalizedMaxValue) {
        setSelectedMinValue(normalizedMinValue);
        setSelectedMaxValue(normalizedMaxValue);
    }

    private final void init() {
        mScaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    public boolean isNotifyWhileDragging() {
        return notifyWhileDragging;
    }

    public void setNotifyWhileDragging(boolean flag) {
        this.notifyWhileDragging = flag;
    }

    public T getAbsoluteMinValue() {
        return absoluteMinValue;
    }

    public T getAbsoluteMaxValue() {
        return absoluteMaxValue;
    }


    public T getSelectedMinValue() {
        return normalizedToValue(normalizedMinValue);
    }


    public void setSelectedMinValue(T value) {

        if (0 == (absoluteMaxValuePrim - absoluteMinValuePrim)) {
            setNormalizedMinValue(0d);
        } else {
            setNormalizedMinValue(valueToNormalized(value));
        }
    }

    public void setSelectedMaxValue(T value) {
        if (0 == (absoluteMaxValuePrim - absoluteMinValuePrim)) {
            setNormalizedMaxValue(1d);
        } else {
            setNormalizedMaxValue(valueToNormalized(value));
        }
    }

    public T getSelectedMaxValue() {
        return normalizedToValue(normalizedMaxValue);
    }


    public void setOnRangeSeekBarChangeListener(OnRangeSeekBarChangeListener<T> listener) {
        this.listener = listener;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        fromUser = true;
        if (isEnableDragging && !isExcute) {
            int pointerIndex;
            final int action = event.getAction();
            switch (action & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:

                    mActivePointerId = event.getPointerId(event.getPointerCount() - 1);
                    pointerIndex = event.findPointerIndex(mActivePointerId);
                    mDownMotionX = event.getX(pointerIndex);//

                    pressedThumb = evalPressedThumb(mDownMotionX);//
                    if (pressedThumb == null) {
                        return super.onTouchEvent(event);
                    }
                    setPressed(true);
                    invalidate();
                    onStartTrackingTouch();//
                    trackTouchEvent(event);
                    attemptClaimDrag();

                    break;
                case MotionEvent.ACTION_MOVE:
                    if (pressedThumb != null) {

                        if (mIsDragging) {
                            trackTouchEvent(event);
                        } else {
                            // Scroll to follow the motion event
                            pointerIndex = event.findPointerIndex(mActivePointerId);
                            final float x = event.getX(pointerIndex);
                            if (Math.abs(x - mDownMotionX) > mScaledTouchSlop) {
                                setPressed(true);
                                invalidate();
                                onStartTrackingTouch();
                                trackTouchEvent(event);
                                attemptClaimDrag();
                            }
                        }
                        if (notifyWhileDragging && listener != null) {
                            listener.onRangeSeekBarValuesChanged(this, getSelectedMinValue(), getSelectedMaxValue());
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (mIsDragging) {
                        trackTouchEvent(event);
                        onStopTrackingTouch();
                        setPressed(false);
                    } else {
                        onStartTrackingTouch();
                        trackTouchEvent(event);
                        onStopTrackingTouch();
                    }
                    pressedThumb = null;
                    invalidate();
                    if (listener != null) {
                        listener.onRangeSeekBarValuesChanged(this, getSelectedMinValue(), getSelectedMaxValue());
                    }
                    break;
                case MotionEvent.ACTION_POINTER_DOWN: {
                    final int index = event.getPointerCount() - 1;
                    // final int index = ev.getActionIndex();
                    mDownMotionX = event.getX(index);
                    mActivePointerId = event.getPointerId(index);
                    invalidate();
                    break;
                }
                case MotionEvent.ACTION_POINTER_UP:
                    onSecondaryPointerUp(event);
                    invalidate();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    if (mIsDragging) {
                        onStopTrackingTouch();
                        setPressed(false);
                    }
                    invalidate(); // see above explanation
                    break;
            }
        }
        return true;
    }

    private final void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = (ev.getAction() & ACTION_POINTER_INDEX_MASK) >> ACTION_POINTER_INDEX_SHIFT;

        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose
            // a new active pointer and adjust accordingly.
            // TODO: Make this decision more intelligent.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mDownMotionX = ev.getX(newPointerIndex);
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    private final void trackTouchEvent(MotionEvent event) {
        final int pointerIndex = event.findPointerIndex(mActivePointerId);
        final float x = event.getX(pointerIndex);
        if (Thumb.MIN.equals(pressedThumb)) {
            // screenToNormalized(x)-->???????0-1???
            setNormalizedMinValue(screenToNormalized(x));
        } else if (Thumb.MAX.equals(pressedThumb)) {
            setNormalizedMaxValue(screenToNormalized(x));
        }
    }

    private void attemptClaimDrag() {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    void onStartTrackingTouch() {
        mIsDragging = true;
    }


    void onStopTrackingTouch() {
        mIsDragging = false;
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 200;
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
            width = MeasureSpec.getSize(widthMeasureSpec);
        }
        int height = thumbImage.getHeight();
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
            height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec)) + (int) getFontHeight(thumbValuePaint)
                    * 3;
        }
        mMY = height / 2;
        setMeasuredDimension(width, height);
        Log.e("MYDoubleSeekbar", "onMeasure");
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
//        // 定义矩阵对象
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.YELLOW);
        thumbValuePaint.setColor(Color.BLUE);

        Bitmap l_bg = BitmapFactory.decodeResource(getResources(), R.drawable.gray_seekbar);

        Bitmap m_bg = BitmapFactory.decodeResource(getResources(), R.drawable.green_seekbar);

        Bitmap r_bg = BitmapFactory.decodeResource(getResources(), R.drawable.gray_seekbar);

        Bitmap m_progress = BitmapFactory.decodeResource(getResources(), R.drawable.green_seekbar);
        //左
        canvas.drawBitmap(l_bg, padding - thumbHalfWidth, 0.5f * (getHeight() - l_bg.getHeight()), paint);

        float bg_middle_left = padding - thumbHalfWidth + l_bg.getWidth() + DensityUtils.dpToPx(20);// 获取初始状态下中间部分的左边界坐标
        float bg_middle_right = getWidth() - padding + thumbHalfWidth - l_bg.getWidth() - DensityUtils.dpToPx(5);// 获取初始状态下中间部分的右边界坐标

        float m_scale = (bg_middle_right - bg_middle_left) / m_progress.getWidth();// 获得中间部分变化比例
        Matrix m_mx = new Matrix();
        m_mx.postScale(m_scale, 1f);
        //中
        Bitmap m_bg_new = Bitmap.createBitmap(m_bg, 0, 0, m_progress.getWidth(), m_progress.getHeight(), m_mx, true);
        canvas.drawBitmap(m_bg_new, bg_middle_left, 0.5f * (getHeight() - m_bg.getHeight()), paint);

        //右
        canvas.drawBitmap(r_bg, bg_middle_right, 0.5f * (getHeight() - r_bg.getHeight()), paint);

        float rangeL = normalizedMinToScreen(normalizedMinValue);
        float rangeR = normalizedMaxToScreen(normalizedMaxValue);
        // float length = rangeR - rangeL;
        float left_scale = rangeL / l_bg.getWidth(); //左边缩放比例
        float pro_scale = (rangeR - rangeL) / m_progress.getWidth(); //中间缩放比例
        float right_scale = (getWidth() - rangeR) / r_bg.getWidth(); //右边缩放比例
        if (left_scale > 0) {
            Matrix left_mx = new Matrix();
            left_mx.postScale(left_scale, 1f);

            Bitmap l_bg_new = Bitmap.createBitmap(l_bg, 0, 0, l_bg.getWidth(), l_bg.getHeight(), left_mx, true);
            canvas.drawBitmap(l_bg_new, padding - thumbHalfWidth, 0.5f * (getHeight() - l_bg.getHeight()), paint);
        }
        if (pro_scale > 0) {

            Matrix pro_mx = new Matrix();
            pro_mx.postScale(pro_scale, 1f);
            try {

                Bitmap m_progress_new = Bitmap.createBitmap(m_progress, 0, 0, m_progress.getWidth(),
                        m_progress.getHeight(), pro_mx, true);

                canvas.drawBitmap(m_progress_new, rangeL + DensityUtils.dpToPx(20), 0.5f * (getHeight() - m_progress.getHeight()), paint);
            } catch (Exception e) {
                Log.e(TAG,
                        "IllegalArgumentException--width=" + m_progress.getWidth() + "Height=" + m_progress.getHeight()
                                + "pro_scale=" + pro_scale, e);

            }

        }
        if (right_scale > 0) {
            Matrix right_mx = new Matrix();
            right_mx.postScale(right_scale, 1f);

            Bitmap r_bg_new = Bitmap.createBitmap(r_bg, 0, 0, r_bg.getWidth(), r_bg.getHeight(), right_mx, true);
            canvas.drawBitmap(r_bg_new, rangeR, 0.5f * (getHeight() - r_bg.getHeight()), paint);
        }


        //绘画左右两个游标
        drawThumb(normalizedMinToScreen(normalizedMinValue), Thumb.MIN.equals(pressedThumb), canvas);

        drawThumb(normalizedMaxToScreen(normalizedMaxValue), Thumb.MAX.equals(pressedThumb), canvas);

        drawThumbMinValue(normalizedMinToScreen(normalizedMinValue), getSelectedMinValue() + "", canvas);

        drawThumbMaxValue(normalizedMaxToScreen(normalizedMaxValue), getSelectedMaxValue() + "", canvas);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable("SUPER", super.onSaveInstanceState());
        bundle.putDouble("MIN", normalizedMinValue);
        bundle.putDouble("MAX", normalizedMaxValue);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable parcel) {
        final Bundle bundle = (Bundle) parcel;
        super.onRestoreInstanceState(bundle.getParcelable("SUPER"));
        normalizedMinValue = bundle.getDouble("MIN");
        normalizedMaxValue = bundle.getDouble("MAX");
    }

    private void drawThumb(float screenCoord, boolean pressed, Canvas canvas) {
        canvas.drawBitmap(pressed ? thumbPressedImage : thumbImage, screenCoord - thumbHalfWidth,
                (float) ((0.5f * getHeight()) - thumbHalfHeight), paint);
    }

    private void drawRightThumb(float screenCoord, boolean pressed, Canvas canvas) {
        canvas.drawBitmap(pressed ? thumbPressedImage : thumbImage, screenCoord - thumbHalfWidth + DensityUtils.dpToPx(2),
                (float) ((0.5f * getHeight()) - thumbHalfHeight), paint);
    }

    /**
     * 绘制左游标
     *
     * @param screenCoord
     * @param text
     * @param canvas
     */
    private void drawThumbMinValue(float screenCoord, String text, Canvas canvas) {
        mMintext = text;
        // 右游标的起始点
        float maxThumbleft = normalizedMaxToScreen(normalizedMaxValue) - thumbHalfWidth;

        // 游标文字区域的右边界位置
        float textRight = screenCoord - thumbHalfWidth + getFontlength(thumbValuePaint, text);

        if (textRight >= maxThumbleft) {
            // 左游标与右游标重叠
            if (pressedThumb == Thumb.MIN && mMintext.equals(mMaxtext)) {
                // touch???min
                canvas.drawText(text, maxThumbleft - getFontlength(thumbValuePaint, text),
                        (float) 0.5f * (getHeight()), thumbValuePaint);

            } else {
                canvas.drawText(text, textRight - getFontlength(thumbValuePaint, text) + DensityUtils.dpToPx(14),
                        (float) 0.5f * (getHeight()), thumbValuePaint);
            }

        } else {

            canvas.drawText(text, screenCoord - thumbHalfWidth + DensityUtils.dpToPx(14), 0.5f * (getHeight()),
                    thumbValuePaint);

        }

    }

    /**
     * 绘制右游标
     *
     * @param screenCoord
     * @param text
     * @param canvas
     */
    private void drawThumbMaxValue(float screenCoord, String text, Canvas canvas) {
        mMaxtext = text;

        // 左游标的右边界
        float minThumbValueRight = normalizedMinToScreen(normalizedMinValue) - thumbHalfWidth
                + getFontlength(thumbValuePaint, "??" + getSelectedMinValue());

        // 游标文字区域的右边界位置
        float textRight = screenCoord - thumbHalfWidth + getFontlength(thumbValuePaint, text);

        if (textRight >= getWidth()) {
            // 右边界超出or等于seekbar宽度

            canvas.drawText(text, getWidth() - getFontlength(thumbValuePaint, text) + DensityUtils.dpToPx(10),
                    (float) 0.5f * (getHeight()), thumbValuePaint);

        } else if ((screenCoord - thumbHalfWidth) <= minThumbValueRight) {
            // 左右游标重叠
            if (pressedThumb == Thumb.MAX && mMintext.equals(mMaxtext)) {
//
                canvas.drawText(text, minThumbValueRight, (float) 0.5f * (getHeight()) - 3,
                        thumbValuePaint);
            } else {
                canvas.drawText(text, screenCoord - thumbHalfWidth + DensityUtils.dpToPx(10),
                        (float) 0.5f * (getHeight()), thumbValuePaint);

            }

        } else {
            //正常情况

            canvas.drawText(text, screenCoord - thumbHalfWidth + DensityUtils.dpToPx(10), 0.5f * (getHeight()),
                    thumbValuePaint);
        }

    }

    private Paint getThumbValuePaint() {
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        p.setAntiAlias(true);//
        p.setFilterBitmap(true);//
        p.setTextSize(25);
        return p;
    }

    private float getFontlength(Paint paint, String str) {
        return paint.measureText(str);
    }

    private float getFontHeight(Paint paint) {
        FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    private Thumb evalPressedThumb(float touchX) {
        Thumb result = null;
        boolean minThumbPressed = isInMinThumbRange(touchX, normalizedMinValue);
        boolean maxThumbPressed = isInMaxThumbRange(touchX, normalizedMaxValue);
        if (minThumbPressed && maxThumbPressed) {
            result = (touchX / getWidth() > 0.5f) ? Thumb.MIN : Thumb.MAX;
        } else if (minThumbPressed) {
            result = Thumb.MIN;
        } else if (maxThumbPressed) {
            result = Thumb.MAX;
        }
        return result;
    }


    private boolean isInMinThumbRange(float touchX, double normalizedMinThumbValue) {

        return Math.abs(touchX - normalizedMinToScreen(normalizedMinThumbValue)) <= thumbHalfWidth;
    }

    private boolean isInMaxThumbRange(float touchX, double normalizedMaxThumbValue) {

        return Math.abs(touchX - normalizedMaxToScreen(normalizedMaxThumbValue)) <= thumbHalfWidth;
    }


    public void setNormalizedMinValue(double value) {
        normalizedMinValue = Math.max(0d, Math.min(1d, Math.min(value, normalizedMaxValue)));
        invalidate();
    }

    public void setNormalizedMaxValue(double value) {
        normalizedMaxValue = Math.max(0d, Math.min(1d, Math.max(value, normalizedMinValue)));
        invalidate();
    }

    @SuppressWarnings("unchecked")
    private T normalizedToValue(double normalized) {
        return (T) numberType.toNumber(absoluteMinValuePrim + normalized
                * (absoluteMaxValuePrim - absoluteMinValuePrim));
    }

    private double valueToNormalized(T value) {
        if (0 == absoluteMaxValuePrim - absoluteMinValuePrim) {
            // prevent division by zero, simply return 0.
            return 0d;
        }
        return (value.doubleValue() - absoluteMinValuePrim) / (absoluteMaxValuePrim - absoluteMinValuePrim);
    }

    private float normalizedMinToScreen(double normalizedCoord) {
        return (float) (padding + normalizedCoord * (getWidth() - 2 * padding) - 7);
        // return (float) (normalizedCoord * getWidth());
    }

    private float normalizedMaxToScreen(double normalizedCoord) {
        return (float) (padding + normalizedCoord * (getWidth() - 2 * padding) + 7);
        // return (float) (normalizedCoord * getWidth());
    }

    private double screenToNormalized(float screenCoord) {
        int width = getWidth();
        if (width <= 2 * padding) {
            return 0d;
        } else {
            double result = (screenCoord - padding) / (width - 2 * padding);
            return Math.min(1d, Math.max(0d, result));
        }
    }

    public interface OnRangeSeekBarChangeListener<T> {
        public void onRangeSeekBarValuesChanged(MyDoubleSeekBar<?> bar, T minValue, T maxValue);
    }

    private static enum Thumb {
        MIN, MAX
    }

    private static enum NumberType {
        LONG, DOUBLE, INTEGER, FLOAT, SHORT, BYTE, BIG_DECIMAL;

        public static <E extends Number> NumberType fromNumber(E value) throws IllegalArgumentException {
            if (value instanceof Long) {
                return LONG;
            }
            if (value instanceof Double) {
                return DOUBLE;
            }
            if (value instanceof Integer) {
                return INTEGER;
            }
            if (value instanceof Float) {
                return FLOAT;
            }
            if (value instanceof Short) {
                return SHORT;
            }
            if (value instanceof Byte) {
                return BYTE;
            }
            if (value instanceof BigDecimal) {
                return BIG_DECIMAL;
            }
            throw new IllegalArgumentException("Number class '" + value.getClass().getName() + "' is not supported");
        }

        public Number toNumber(double value) {
            // this???????÷???????????????е??????????
            switch (this) {
                case LONG:
                    return new Long((long) value);
                case DOUBLE:
                    return value;
                case INTEGER:
                    return new Integer((int) value);
                case FLOAT:
                    return new Float(value);
                case SHORT:
                    return new Short((short) value);
                case BYTE:
                    return new Byte((byte) value);
                case BIG_DECIMAL:
                    return new BigDecimal(value);
            }
            throw new InstantiationError("can't convert " + this + " to a Number object");
        }
    }
}