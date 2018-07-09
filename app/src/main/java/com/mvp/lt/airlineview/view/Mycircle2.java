package com.mvp.lt.airlineview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.mvp.lt.airlineview.utils.RouteUtils2;

/**
 * $activityName
 * 全屏拖动的View
 *
 * @author LiuTao
 * @date 2018/7/7/007
 * ┌───┐   ┌───┬───┬───┬───┐ ┌───┬───┬───┬───┐ ┌───┬───┬───┬───┐ ┌───┬───┬───┐
 * │Esc│   │ F1│ F2│ F3│ F4│ │ F5│ F6│ F7│ F8│ │ F9│F10│F11│F12│ │P/S│S L│P/B│  ┌┐    ┌┐    ┌┐
 * └───┘   └───┴───┴───┴───┘ └───┴───┴───┴───┘ └───┴───┴───┴───┘ └───┴───┴───┘  └┘    └┘    └┘
 * ┌───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───────┐ ┌───┬───┬───┐ ┌───┬───┬───┬───┐
 * │~ `│! 1│@ 2│# 3│$ 4│% 5│^ 6│& 7│* 8│( 9│) 0│_ -│+ =│ BacSp │ │Ins│Hom│PUp│ │N L│ / │ * │ - │
 * ├───┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─────┤ ├───┼───┼───┤ ├───┼───┼───┼───┤
 * │ Tab │ Q │ W │ E │ R │ T │ Y │ U │ I │ O │ P │{ [│} ]│ | \ │ │Del│End│PDn│ │ 7 │ 8 │ 9 │   │
 * ├─────┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴─────┤ └───┴───┴───┘ ├───┼───┼───┤ + │
 * │ Caps │ A │ S │ D │ F │ G │ H │ J │ K │ L │: ;│" '│ Enter  │               │ 4 │ 5 │ 6 │   │
 * ├──────┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴────────┤     ┌───┐     ├───┼───┼───┼───┤
 * │ Shift  │ Z │ X │ C │ V │ B │ N │ M │< ,│> .│? /│  Shift   │     │ ↑ │     │ 1 │ 2 │ 3 │   │
 * ├─────┬──┴─┬─┴──┬┴───┴───┴───┴───┴───┴──┬┴───┼───┴┬────┬────┤ ┌───┼───┼───┐ ├───┴───┼───┤ E││
 * │ Ctrl│    │Alt │         Space         │ Alt│    │    │Ctrl│ │ ← │ ↓ │ → │ │   0   │ . │←─┘│
 * └─────┴────┴────┴───────────────────────┴────┴────┴────┴────┘ └───┴───┴───┘ └───────┴───┴───┘
 */


public class Mycircle2 extends View {
    private Paint paint;
    private int rawX;
    private int rawY;
    private int wid;
    private int he;
    int statusBarHeight1 = -1;
    private int mWidth, mHeight, centerWidth, centerHeight;        // 宽高
    private int deviceHeight;
    private int deviceWidth;        // 宽高
    private Point mStartPoint;
    private Point mCenterPoint;
    private int mStartAngle;

    //构造方法，一般会重写三个
    //用于初始化一些数据，或者其他东西
    public Mycircle2(Context context) {
        this(context, null);
    }

    public Mycircle2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Mycircle2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化画笔
        //抗锯齿
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //设置画笔
        paint.setColor(Color.GREEN);//设置画笔颜色
        paint.setStrokeWidth(3);//设置画笔粗细

        //获取整个屏幕的高度和宽度
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        wid = displayMetrics.widthPixels;
        he = displayMetrics.heightPixels;

        //获取status_bar_height资源的ID  获取状态栏的高度
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
        }
    }

    //重写绘制的方法
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        centerWidth = deviceWidth / 2;
        centerHeight = deviceHeight / 2;
        mCenterPoint = new Point(centerWidth, centerHeight);
        mStartPoint = new Point(centerWidth, 0);
        if (mWidth > mHeight) {
            canvas.drawCircle(mWidth / 2, mHeight / 2, mHeight / 2, paint);
        } else {
            canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth / 2, paint);
        }

        getAngle(this.getLeft(), this.getTop());
      /*  paint.setColor(Color.LTGRAY);
        //实例化路径
        Path path = new Path();
        path.moveTo(80, 200);// 此点为多边形的起点
        path.lineTo(120, 250);
        path.lineTo(80, 250);
        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, paint);*/
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
//拖动事件
    //拖动的实现原理：
    /**
     * 每个View在屏幕上都有个坐标，也就是上下左右边距，在屏幕上都有（x，y）坐标。如果坐标移动，那么View的位置也会移动
     * ，这是比较好理解的。
     * 我们手指在手机屏幕上滑动的时候，手指的坐标也是移动的。
     * 我们只需要获得手指从按下到离开过程中的距离差，然后将距离差加到原来的坐标上就可以是实现控件的移动。
     * 如果要实现拖动，那么在滑动的过程中，不断的获取距离差，不断的加到原来的坐标就可以了。
     * 注意：
     *     这里的移动是相对于屏幕的，所以我们获取坐标应该是绝对坐标，而不是相对坐标
     *     event.getRawX() ---- 获取绝对X坐标
     *     event.getRawY() ---- 获取绝对Y坐标
     *
     *     event.getX()-------- 获取相对坐标x
     *     event.getY()-------- 获取相对坐标Y
     *
     */

    // onTouchEvent 处理触摸事件
    //Touch事件：1.按下ACTION_DOWN，2.抬起ACTION_UP，3 滑动 ACTION_MOVE 4.取消ACTION_CANCEL
    //获取触摸点的坐标
    //绝对坐标---相对于屏幕来说
    //相对坐标---相对于自己
    //event.getAction()   获取事件

    /**
     * View大小确定
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        deviceHeight = displayMetrics.heightPixels;
        deviceWidth = displayMetrics.widthPixels;
        Log.e("屏幕", deviceHeight + "" + deviceWidth);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        boolean isMove = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //获取开始的坐标

                rawX = (int) event.getRawX();
                rawY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                //获取移动时候的坐标
                int yX = (int) event.getRawX();
                int yY = (int) event.getRawY();
                getAngle(yX, yY);
                //减去手指按下时候的坐标
                //得到移动的间距
                int jX = yX - rawX;
                int jY = yY - rawY;
                //将间距，加到原来的坐标（上下左右）
                int l = getLeft() + jX;
                int r = getRight() + jX;
                int t = getTop() + jY;
                int b = getBottom() + jY;

                //判断
                if (l < 0) {
                    l = 0;
                    r = getWidth();
                }
                if (t < 0) {
                    t = 0;
                    b = getHeight();
                }

                if (r > wid) {
                    r = wid;
                    l = wid - getHeight();
                }
                //如果移动到最下边,就判断是否等于屏幕高度减去状态栏高度
                if (b > he - statusBarHeight1) {
                    //赋值
                    b = he - statusBarHeight1;
                    t = b - getHeight();

                }

                //重新赋值给布局
                layout(l, t, r, b);//规定了View的位置
                //将lastX，lastY重新赋值
                rawX = yX;
                rawY = yY;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true; //返回true代表自己处理事件

    }

    private void getAngle(int yX, int yY) {
        //计算角度Ro
        Point endPoint = new Point(yX, yY);
        int angle = (int) RouteUtils2.angle(mCenterPoint, mStartPoint, endPoint);
        if (mOnDateDragChangeListener != null)
            mOnDateDragChangeListener.onDateChangeDrag(angle);
    }

    public interface OnDateDragChangeListener {
        void onDateChangeDrag(int angle);
    }

    private OnDateDragChangeListener mOnDateDragChangeListener;

    public OnDateDragChangeListener getOnDateDragChangeListener() {
        return mOnDateDragChangeListener;
    }

    public void setOnDateDragChangeListener(OnDateDragChangeListener onDateDragChangeListener) {
        mOnDateDragChangeListener = onDateDragChangeListener;
    }
}
