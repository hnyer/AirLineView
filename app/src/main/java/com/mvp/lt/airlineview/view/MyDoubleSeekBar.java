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
 *
 * @param <T> The Number type of the range values. One of Long, Double, Integer,
 *            Float, Short, Byte or BigDecimal.
 * @author Stephan Tittel (stephan.tittel@kom.tu-darmstadt.de)
 * @author Peter Sinnott (psinnott@gmail.com)
 * @author Thomas Barrasso (tbarrasso@sevenplusandroid.org)
 * @author Yao (chuan_28049@126.com)
 * @author Victor Shi (2015/8/3)
 */
public class MyDoubleSeekBar<T extends Number> extends android.support.v7.widget.AppCompatImageView {
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
    private double normalizedMaxValue = 1d;// normalized??????--?????????????????????Χ??0-1
    private Thumb pressedThumb = null;
    private boolean notifyWhileDragging = false;
    private OnRangeSeekBarChangeListener<T> listener;

    /**
     * Default color of a {@link MyDoubleSeekBar}, #FF33B5E5. This is also known as
     * "Ice Cream Sandwich" blue.
     */
    public static final int DEFAULT_COLOR = Color.argb(0xFF, 0x33, 0xB5, 0xE5);

    /**
     * An invalid pointer id.
     */
    public static final int INVALID_POINTER_ID = 255;

    // Localized constants from MotionEvent for compatibility
    // with API < 8 "Froyo".
    public static final int ACTION_POINTER_UP = 0x6, ACTION_POINTER_INDEX_MASK = 0x0000ff00,
            ACTION_POINTER_INDEX_SHIFT = 8;

    private float mDownMotionX;// ???touchEvent???????X????
    private int mActivePointerId = INVALID_POINTER_ID;

    /**
     * On touch, this offset plus the scaled value from the position of the
     * touch will form the progress value. Usually 0.
     */
    float mTouchProgressOffset;

    private int mScaledTouchSlop;
    private boolean mIsDragging;
    private int mMY;

    /**
     * Creates a new RangeSeekBar.
     *
     * @param absoluteMinValue The minimum value of the selectable range.
     * @param absoluteMaxValue The maximum value of the selectable range.
     * @param context
     * @throws IllegalArgumentException Will be thrown if min/max value type is not one of Long,
     *                                  Double, Integer, Float, Short, Byte or BigDecimal.
     */
    public MyDoubleSeekBar(T absoluteMinValue, T absoluteMaxValue, Context context) throws IllegalArgumentException {
        super(context);
        this.absoluteMinValue = absoluteMinValue;
        this.absoluteMaxValue = absoluteMaxValue;
        absoluteMinValuePrim = absoluteMinValue.doubleValue();//?
        absoluteMaxValuePrim = absoluteMaxValue.doubleValue();
        numberType = NumberType.fromNumber(absoluteMinValue);// ???????????????????

        // make RangeSeekBar focusable. This solves focus handling issues in
        // case EditText widgets are being used along with the RangeSeekBar
        // within ScollViews.
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
        init();
        invalidate();
    }

    private final void init() {
        // ??????????????????????
        mScaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    /**
     * ????activity????????????????????????log??????????false?????
     */
    public boolean isNotifyWhileDragging() {
        return notifyWhileDragging;
    }

    /**
     * Should the widget notify the listener callback while the user is still
     * dragging a thumb? Default is false.
     *
     * @param flag
     */
    public void setNotifyWhileDragging(boolean flag) {
        this.notifyWhileDragging = flag;
    }

    /**
     * Returns the absolute minimum value of the range that has been set at
     * construction time.
     *
     * @return The absolute minimum value of the range.
     */
    public T getAbsoluteMinValue() {
        return absoluteMinValue;
    }

    /**
     * Returns the absolute maximum value of the range that has been set at
     * construction time.
     *
     * @return The absolute maximum value of the range.
     */
    public T getAbsoluteMaxValue() {
        return absoluteMaxValue;
    }

    /**
     * Returns the currently selected min value.
     *
     * @return The currently selected min value.
     */
    public T getSelectedMinValue() {
        return normalizedToValue(normalizedMinValue);
    }

    /**
     * Sets the currently selected minimum value. The widget will be invalidated
     * and redrawn.
     *
     * @param value The Number value to set the minimum value to. Will be clamped
     *              to given absolute minimum/maximum range.
     */
    public void setSelectedMinValue(T value) {
        // in case absoluteMinValue == absoluteMaxValue, avoid division by zero
        // when normalizing.
        if (0 == (absoluteMaxValuePrim - absoluteMinValuePrim)) {
            // activity?????????????С????
            setNormalizedMinValue(0d);
        } else {
            setNormalizedMinValue(valueToNormalized(value));
        }
    }

    /**
     * Returns the currently selected max value.
     *
     * @return The currently selected max value.
     */
    public T getSelectedMaxValue() {
        return normalizedToValue(normalizedMaxValue);
    }

    /**
     * Sets the currently selected maximum value. The widget will be invalidated
     * and redrawn.
     *
     * @param value The Number value to set the maximum value to. Will be clamped
     *              to given absolute minimum/maximum range.
     */
    public void setSelectedMaxValue(T value) {
        // in case absoluteMinValue == absoluteMaxValue, avoid division by zero
        // when normalizing.
        if (0 == (absoluteMaxValuePrim - absoluteMinValuePrim)) {
            setNormalizedMaxValue(1d);
        } else {
            setNormalizedMaxValue(valueToNormalized(value));
        }
    }

    /**
     * Registers given listener callback to notify about changed selected
     * values.
     *
     * @param listener The listener to notify about changed selected values.
     */
    public void setOnRangeSeekBarChangeListener(OnRangeSeekBarChangeListener<T> listener) {
        this.listener = listener;
    }

    /**
     * Handles thumb selection and movement. Notifies listener callback on
     * certain events.
     * <p>
     * <p>
     * ACTION_MASK??Android??????????????????????????????????????????????ɡ?
     * ??onTouchEvent(MotionEvent event)?У????switch
     * (event.getAction())???????ACTION_DOWN??ACTION_UP????????switch
     * (event.getAction() & MotionEvent.ACTION_MASK)
     * ????????????????ACTION_POINTER_DOWN??ACTION_POINTER_UP?????
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!isEnabled())
            return false;

        int pointerIndex;// ?????????index

        final int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:

                mActivePointerId = event.getPointerId(event.getPointerCount() - 1);
                pointerIndex = event.findPointerIndex(mActivePointerId);
                mDownMotionX = event.getX(pointerIndex);//

                pressedThumb = evalPressedThumb(mDownMotionX);//
                // Only handle thumb presses.
                if (pressedThumb == null)
                    return super.onTouchEvent(event);
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
                    // Touch up when we never crossed the touch slop threshold
                    // should be interpreted as a tap-seek to that location.
                    onStartTrackingTouch();
                    trackTouchEvent(event);
                    onStopTrackingTouch();
                }

                pressedThumb = null;// ???????????touch????thumb???
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

    /**
     * ?????touch????????view
     *
     * @param event
     */
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

    /**
     * Tries to claim the user's drag motion, and requests disallowing any
     * ancestors from stealing events in the drag.
     * <p>
     * ????????view?????????????drag
     */
    private void attemptClaimDrag() {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    /**
     * This is called when the user has started touching this widget.
     */
    void onStartTrackingTouch() {
        mIsDragging = true;
    }

    /**
     * This is called when the user either releases his touch or the touch is
     * canceled.
     */
    void onStopTrackingTouch() {
        mIsDragging = false;
    }

    /**
     * Ensures correct size of the widget.
     */
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
    }

    /**
     * Draws the widget on the given canvas.
     */
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLUE);
        thumbValuePaint.setColor(Color.BLUE);
//        Bitmap l_bg = BitmapFactory.decodeResource(getResources(), R.drawable.red_seekbar);
//
//        Bitmap m_bg = BitmapFactory.decodeResource(getResources(), R.drawable.yellow_seekbar);
//
//        Bitmap r_bg = BitmapFactory.decodeResource(getResources(), R.drawable.green_seekbar);
//
//        Bitmap m_progress = BitmapFactory.decodeResource(getResources(), R.drawable.yellow_seekbar);

        canvas.drawLine(padding - thumbHalfWidth + +DensityUtils.dpToPx(4), mMY, 0.5f * (getHeight()), mMY, paint);
        float bg_middle_left = padding - thumbHalfWidth;// 获取初始状态下中间部分的左边界坐标
        float bg_middle_right = getWidth() - padding + thumbHalfWidth;// 获取初始状态下中间部分的右边界坐标

        canvas.drawLine(bg_middle_left+DensityUtils.dpToPx(20), mMY, 0.5f * (getHeight()), mMY, paint);

        canvas.drawLine(bg_middle_right-DensityUtils.dpToPx(5), mMY, 0.5f * (getHeight()), mMY, paint);
        float rangeL = normalizedToScreen(normalizedMinValue);
        float rangeR = normalizedToScreen(normalizedMaxValue);

        float left_scale = rangeL; //左边缩放比例
        float pro_scale = (rangeR - rangeL); //中间缩放比例
        float right_scale = (getWidth() - rangeR); //右边缩放比例
        if (left_scale > 0) {
            canvas.drawLine(padding - thumbHalfWidth+DensityUtils.dpToPx(20), mMY, 0.5f * (getHeight()), mMY, paint);
        }
        if (pro_scale > 0) {

            Matrix pro_mx = new Matrix();
            pro_mx.postScale(pro_scale, 1f);
            try {
                canvas.drawLine(rangeL+DensityUtils.dpToPx(20), mMY, 0.5f * (getHeight()), mMY, paint);
            } catch (Exception e) {


            }

        }

        if (right_scale > 0) {
            Matrix right_mx = new Matrix();
            right_mx.postScale(right_scale, 1f);
            canvas.drawLine(rangeR, mMY, 0.5f * (getHeight()), mMY, paint);
        }

        //绘画左右两个游标
        drawThumbMinValue(normalizedToScreen(normalizedMinValue), getSelectedMinValue() + "", canvas);

        drawThumbMaxValue(normalizedToScreen(normalizedMaxValue), getSelectedMaxValue() + "", canvas);

        drawThumb(normalizedToScreen(normalizedMinValue), Thumb.MIN.equals(pressedThumb), canvas);

        drawRightThumb(normalizedToScreen(normalizedMaxValue), Thumb.MAX.equals(pressedThumb), canvas);

    }

    /**
     * Overridden to save instance state when device orientation changes. This
     * method is called automatically if you assign an id to the RangeSeekBar
     * widget using the {@link #setId(int)} method. Other members of this class
     * than the normalized min and max values don't need to be saved.
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable("SUPER", super.onSaveInstanceState());
        bundle.putDouble("MIN", normalizedMinValue);
        bundle.putDouble("MAX", normalizedMaxValue);
        return bundle;
    }

    /**
     * Overridden to restore instance state when device orientation changes.
     * This method is called automatically if you assign an id to the
     * RangeSeekBar widget using the {@link #setId(int)} method.
     */
    @Override
    protected void onRestoreInstanceState(Parcelable parcel) {
        final Bundle bundle = (Bundle) parcel;
        super.onRestoreInstanceState(bundle.getParcelable("SUPER"));
        normalizedMinValue = bundle.getDouble("MIN");
        normalizedMaxValue = bundle.getDouble("MAX");
    }

    /**
     * Draws the "normal" resp. "pressed" thumb image on specified x-coordinate.
     *
     * @param screenCoord The x-coordinate in screen space where to draw the image.
     * @param pressed     Is the thumb currently in "pressed" state?
     * @param canvas      The canvas to draw upon.
     */
    private void drawThumb(float screenCoord, boolean pressed, Canvas canvas) {
        canvas.drawBitmap(pressed ? thumbPressedImage : thumbImage, screenCoord - thumbHalfWidth,
                (float) ((0.5f * getHeight()) - thumbHalfHeight), paint);
    }
    private void drawRightThumb(float screenCoord, boolean pressed, Canvas canvas) {
        canvas.drawBitmap(pressed ? thumbPressedImage : thumbImage, screenCoord - thumbHalfWidth+DensityUtils.dpToPx(2),
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

        // 右游标的起始点
        float maxThumbleft = normalizedToScreen(normalizedMaxValue) - thumbHalfWidth;

        // 游标文字区域的右边界位置
        float textRight = screenCoord - thumbHalfWidth + getFontlength(thumbValuePaint, text);

        if (textRight >= maxThumbleft) {
            // 左游标与右游标重叠
            if (pressedThumb == Thumb.MIN) {
                // touch???min
                canvas.drawText(text, maxThumbleft - getFontlength(thumbValuePaint, text) + DensityUtils.dpToPx(15),
                        (float) ((0.4f * getHeight()) - thumbHalfHeight) - 3, thumbValuePaint);

            } else {
                canvas.drawText(text, textRight - getFontlength(thumbValuePaint, text) + DensityUtils.dpToPx(15),
                        (float) ((0.4f * getHeight()) - thumbHalfHeight) - 3, thumbValuePaint);
            }

        } else {

            canvas.drawText(text, screenCoord - thumbHalfWidth + DensityUtils.dpToPx(15), (float) ((0.4f * getHeight()) - thumbHalfHeight) - 3,
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

        // 左游标的右边界
        float minThumbValueRight = normalizedToScreen(normalizedMinValue) - thumbHalfWidth
                + getFontlength(thumbValuePaint, "??" + getSelectedMinValue());

        // 游标文字区域的右边界位置
        float textRight = screenCoord - thumbHalfWidth + getFontlength(thumbValuePaint, text);

        if (textRight >= getWidth()) {
            // 右边界超出or等于seekbar宽度

            canvas.drawText(text, getWidth() - getFontlength(thumbValuePaint, text) + DensityUtils.dpToPx(1),
                    (float) ((0.4f * getHeight()) - thumbHalfHeight) - 3, thumbValuePaint);

        } else if ((screenCoord - thumbHalfWidth) <= minThumbValueRight) {
            // 左右游标重叠
            if (pressedThumb == Thumb.MAX) {

                canvas.drawText(text, minThumbValueRight + DensityUtils.dpToPx(15), (float) ((0.4f * getHeight()) - thumbHalfHeight) - 3,
                        thumbValuePaint);

            } else {
                canvas.drawText(text, screenCoord - thumbHalfWidth + DensityUtils.dpToPx(15),
                        (float) ((0.4f * getHeight()) - thumbHalfHeight) - 3, thumbValuePaint);

            }

        } else {
            //正常情况

            canvas.drawText(text, screenCoord - thumbHalfWidth + DensityUtils.dpToPx(15), (float) ((0.4f * getHeight()) - thumbHalfHeight) - 3,
                    thumbValuePaint);
        }

    }

    /**
     * ???thumb???paint
     */
    private Paint getThumbValuePaint() {
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        p.setAntiAlias(true);// ??????
        p.setFilterBitmap(true);// ??λ????????????
        p.setTextSize(25);

        return p;
    }

    /**
     * @return ???????????????????????
     */
    private float getFontlength(Paint paint, String str) {
        return paint.measureText(str);
    }

    /**
     * @return ????????????????
     */
    private float getFontHeight(Paint paint) {
        FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    /**
     * Decides which (if any) thumb is touched by the given x-coordinate.
     * <p>
     * eval ??n. ?????????????????????
     *
     * @param touchX The x-coordinate of a touch event in screen space.
     * @return The pressed thumb or null if none has been
     * touched.//??touch????????????????С?
     */
    private Thumb evalPressedThumb(float touchX) {
        Thumb result = null;
        boolean minThumbPressed = isInThumbRange(touchX, normalizedMinValue);// ?????????????С?????Χ??
        boolean maxThumbPressed = isInThumbRange(touchX, normalizedMaxValue);
        if (minThumbPressed && maxThumbPressed) {
            // if both thumbs are pressed (they lie on top of each other),
            // choose the one with more room to drag. this avoids "stalling" the
            // thumbs in a corner, not being able to drag them apart anymore.

            // ???????thumbs????????????ж??????????????????
            // ?????????????????ж??touch??????С?thumb??????ж??touch????????thumb
            result = (touchX / getWidth() > 0.5f) ? Thumb.MIN : Thumb.MAX;
        } else if (minThumbPressed) {
            result = Thumb.MIN;
        } else if (maxThumbPressed) {
            result = Thumb.MAX;
        }
        return result;
    }

    /**
     * Decides if given x-coordinate in screen space needs to be interpreted as
     * "within" the normalized thumb x-coordinate.
     *
     * @param touchX               The x-coordinate in screen space to check.
     * @param normalizedThumbValue The normalized x-coordinate of the thumb to check.
     * @return true if x-coordinate is in thumb range, false otherwise.
     */
    private boolean isInThumbRange(float touchX, double normalizedThumbValue) {

        return Math.abs(touchX - normalizedToScreen(normalizedThumbValue)) <= thumbHalfWidth;
    }

    /**
     * Sets normalized min value to value so that 0 <= value <= normalized max
     * value <= 1. The View will get invalidated when calling this method.
     *
     * @param value The new normalized min value to set.
     */
    public void setNormalizedMinValue(double value) {
        normalizedMinValue = Math.max(0d, Math.min(1d, Math.min(value, normalizedMaxValue)));
        invalidate();
    }

    /**
     * Sets normalized max value to value so that 0 <= normalized min value <=
     * value <= 1. The View will get invalidated when calling this method.
     *
     * @param value The new normalized max value to set.
     */
    public void setNormalizedMaxValue(double value) {
        normalizedMaxValue = Math.max(0d, Math.min(1d, Math.max(value, normalizedMinValue)));
        invalidate();
    }

    /**
     * Converts a normalized value to a Number object in the value space between
     * absolute minimum and maximum.
     *
     * @param normalized ?????????????λ??????????????λ???????????????м????????????????0.5
     * @return ??????????????????ó?????????????
     */
    @SuppressWarnings("unchecked")
    private T normalizedToValue(double normalized) {
        return (T) numberType.toNumber(absoluteMinValuePrim + normalized
                * (absoluteMaxValuePrim - absoluteMinValuePrim));
    }

    /**
     * Converts the given Number value to a normalized double.
     *
     * @param value The Number value to normalize.
     * @return The normalized double.
     */
    private double valueToNormalized(T value) {
        if (0 == absoluteMaxValuePrim - absoluteMinValuePrim) {
            // prevent division by zero, simply return 0.
            return 0d;
        }
        return (value.doubleValue() - absoluteMinValuePrim) / (absoluteMaxValuePrim - absoluteMinValuePrim);
    }

    /**
     * Converts a normalized value into screen space.
     *
     * @param normalizedCoord The normalized value to convert.
     * @return The converted value in screen space.//???????????????????????
     */
    private float normalizedToScreen(double normalizedCoord) {
        // getWidth() - 2 * padding --> ????View?????????padding??
        // ????????thumb????,??????thumb????????Χ????

        // normalizedCoord * (getWidth() - 2 * padding)
        // ?????????????????????????????x?????

        // padding + normalizedCoord * (getWidth() - 2 * padding)
        // ??????????????x?????

        return (float) (padding + normalizedCoord * (getWidth() - 2 * padding));
        // return (float) (normalizedCoord * getWidth());
    }

    /**
     * Converts screen space x-coordinates into normalized values.
     *
     * @param screenCoord The x-coordinate in screen space to convert.
     * @return The normalized value.
     */
    private double screenToNormalized(float screenCoord) {
        int width = getWidth();
        if (width <= 2 * padding) {
            // prevent division by zero, simply return 0.
            return 0d;
        } else {
            double result = (screenCoord - padding) / (width - 2 * padding);
            return Math.min(1d, Math.max(0d, result));// ????????0-1????????????????ж????????
        }
    }

    /**
     * Callback listener interface to notify about changed range values.
     *
     * @param <T> The Number type the RangeSeekBar has been declared with.
     * @author Stephan Tittel (stephan.tittel@kom.tu-darmstadt.de)
     */
    public interface OnRangeSeekBarChangeListener<T> {
        public void onRangeSeekBarValuesChanged(MyDoubleSeekBar<?> bar, T minValue, T maxValue);
    }

    /**
     * Thumb constants (min and max). ??????????????????????????????????????????????С?
     */
    private static enum Thumb {
        MIN, MAX
    }

    ;

    /**
     * Utility enumaration used to convert between Numbers and doubles.
     *
     * @author Stephan Tittel (stephan.tittel@kom.tu-darmstadt.de)
     */
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