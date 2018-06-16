package com.mvp.lt.airlineview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.mvp.lt.airlineview.utils.RouteUtlis;

import java.util.ArrayList;
import java.util.List;

/**
 * $name
 * 自定义view航点 难点 拖动功能 点的确定
 *
 * @author ${LiuTa o}
 * @date 2017/12/28/028
 */

public class DragScaleView extends View implements View.OnTouchListener {
    private int centerX;                  //中心X
    private int centerY;                  //中心Y

    protected int screenWidth;
    protected int screenHeight;
    protected int lastX;
    protected int lastY;
    private int oriLeft;  //左边线
    private int oriRight; //右边线
    private int oriTop;   //顶部线
    private int oriBottom;//底部线
    private int dragDirection;
    private static final int TOP = 0x15;
    private static final int LEFT = 0x16;
    private static final int BOTTOM = 0x17;
    private static final int RIGHT = 0x18;
    private static final int LEFT_TOP = 0x11;
    private static final int RIGHT_TOP = 0x12;
    private static final int LEFT_BOTTOM = 0x13;
    private static final int RIGHT_BOTTOM = 0x14;
    private static final int CENTER = 0x19;
    private int offset = 20;
    protected Paint paint = new Paint();
    protected Paint paint2 = new Paint();
    private static ArrayList<Path> mPaths = new ArrayList<Path>();
    /**
     * 起始点
     */
    private Point startPoint;
    private Point referPoint;


    List<Point> mPonitsList2 = new ArrayList<>();
    /**
     * 点集合
     */
    List<Point> mPonitsList = new ArrayList<>();
    private Path mPath;
    private Path mInitPath;
    private Path mPath2 = new Path();
    private Path mDivPath = new Path();
    private Point mP1;
    private Point mP2;
    private Point mP4;
    private Point mP3;
    private Path mPathline;
    /**
     * 确认点的个数
     */
    private boolean isOk = false;
    /**
     * 点确定之后，是否再能选点
     */
    private boolean isDraw = true;
    private String mFlag = "d";
    private Paint mPaint3;
    private Path mMPathAirLine;

    public List<Point> getAllEdgPointList() {
        return mAllEdgPointList;
    }

    public void setAllEdgPointList(List<Point> allEdgPointList) {
        mAllEdgPointList = allEdgPointList;
    }

    private List<Point> mAllEdgPointList;

    public List<Point> getPonitsList() {
        return mPonitsList;
    }

    public void setPonitsList(List<Point> ponitsList) {
        mPonitsList = ponitsList;
    }


    protected void initScreenW_H() {
        screenHeight = getResources().getDisplayMetrics().heightPixels - getStatusBarHeight();
        screenWidth = getResources().getDisplayMetrics().widthPixels;
    }

    public int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        return getResources().getDimensionPixelSize(resourceId);
    }

    public DragScaleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnTouchListener(this);
        initScreenW_H();
    }

    public DragScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
        initScreenW_H();
    }

    public DragScaleView(Context context) {
        super(context);
        setOnTouchListener(this);
        initScreenW_H();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = 200;
        int width = 200;
        final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSpecSize = MeasureSpec.getMode(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(width, height);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(width, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, height);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawInit(canvas);
        drawCanvas(canvas);
    }

    private void drawInit(Canvas canvas) {
        mMPathAirLine = new Path();

        mInitPath = new Path();
        mPath = new Path();
        //STROKE 空心 FILL填充
        //画笔二
        paint2 = new Paint();
        paint2.setColor(Color.BLUE);
        paint2.setStyle(Paint.Style.FILL);
        paint2.setStrokeWidth(10);
        //顶点画笔
        mPaint3 = new Paint();
        mPaint3.setColor(Color.GREEN);
        mPaint3.setStyle(Paint.Style.STROKE);
        mPaint3.setStrokeWidth(10);

        paint.setColor(Color.RED);
        paint.setStrokeWidth(4.0f);
        paint.setStyle(Paint.Style.STROKE);

        mP1 = new Point(offset, offset);
        mP2 = new Point(getWidth() - offset, offset);
        mP4 = new Point(offset, getHeight() - offset);
        mP3 = new Point(getWidth() - offset, getHeight() - offset);
        mPonitsList.add(mP1);
        mPonitsList.add(mP2);
        mPonitsList.add(mP3);
        mPonitsList.add(mP4);

        mInitPath.moveTo(mP1.x, mP1.y);
        mInitPath.lineTo(mP2.x, mP2.y);
        mInitPath.lineTo(mP3.x, mP3.y);
        mInitPath.lineTo(mP4.x, mP4.y);
        mInitPath.close();
//        canvas.drawPath(mPath, paint);
//        canvas.drawCircle(mP1.x, mP1.y, 10, paint2);
//        canvas.drawCircle(mP2.x, mP2.y, 10, paint2);
//        canvas.drawCircle(mP3.x, mP3.y, 10, paint2);
//        canvas.drawCircle(mP4.x, mP4.y, 10, paint2);
//        canvas.drawPath(mInitPath, paint);

    }
  //
    private void drawCanvas(Canvas canvas) {
        if (mPonitsList2 != null && mPonitsList2.size() > 0) {
            for (int i = 0; i < mPonitsList2.size(); i++) {
                Log.e("drawCanvas", "x:" + mPonitsList2.get(i).x + ",y:" + mPonitsList2.get(i).y);
                //顶点
                canvas.drawCircle(mPonitsList2.get(i).x, mPonitsList2.get(i).y, 10, mPaint3);
                if (i == 0) {
                    mPath.moveTo(mPonitsList2.get(i).x, mPonitsList2.get(i).y);
                } else {
                    mPath.lineTo(mPonitsList2.get(i).x, mPonitsList2.get(i).y);
                }

            }

        }
        mPath.close();
        canvas.drawPath(mPath, paint);
        Log.e("TAG", "mPonitsList2:" + mPonitsList2.size());
        if (mPonitsList2.size() > 3) {
            Point startPoint = mPonitsList2.get(0);
            Point endPoint = RouteUtlis.getMaxDisPonit(mPonitsList2.get(0), mPonitsList2, mFlag);
            // 对角线上的等分点
            List<Point> list = RouteUtlis.getLinePoint(startPoint, endPoint, 50);
            for (int i = 0; i < list.size(); i++) {
                Log.e("TAG", "等分点：x:" + list.get(i).x + ",y:" + list.get(i).y);
                canvas.drawCircle(list.get(i).x, list.get(i).y, 10, paint2);
            }
            List<DivLinePoint> divLinePointList = RouteUtlis.getDivLineAndPoint(startPoint, endPoint, list);
            List<LineGroup> lineGroupList = RouteUtlis.getVerticalLinePiont2(mPonitsList2);
            Log.e("TAG", "lineGroupList:" + lineGroupList.size());
            mAllEdgPointList = RouteUtlis.getAllEdgPoint(startPoint, divLinePointList, lineGroupList, endPoint);
            for (int i = 0; i < mAllEdgPointList.size(); i++) {
                canvas.drawCircle(mAllEdgPointList.get(i).x, mAllEdgPointList.get(i).y, 10, paint);
                if (i == 0)
                    mDivPath.moveTo(mAllEdgPointList.get(i).x, mAllEdgPointList.get(i).y);
                mDivPath.lineTo(mAllEdgPointList.get(i).x, mAllEdgPointList.get(i).y);
                Log.e("TAG", "allEdgPointList:" + mAllEdgPointList.get(i).x + "," + mAllEdgPointList.get(i).y);
            }
//            mDivPath.moveTo(allEdgPointList.get(0).x, allEdgPointList.get(0).y);
//            mDivPath.lineTo(allEdgPointList.get(1).x, allEdgPointList.get(1).y);

            canvas.drawPath(mDivPath, paint);
        } else if (mPonitsList2.size() == 3) {
            drawTriangle2(canvas, mPonitsList2.get(0), mPonitsList2.get(1), mPonitsList2.get(2));
            //mPath2.close();
            //drawTriangle();
            // drawTriangle(canvas);
            // drawTriangle2();
            // drawSqure();
//                drawTriangle();

        }
        isOk = false;
    }

    /**
     * 以面为起始面
     *
     * @param referPoint
     * @param ponit1
     * @param ponit2
     */
    private void drawTriangle2(Canvas canvas, Point referPoint, Point ponit1, Point ponit2) {
        //参考点
        //referPoint = mPonitsList2.get(0);
        //两条线上的点
        List<Point> dis1Lists = new ArrayList<>();
        List<Point> dis2Lists = new ArrayList<>();

        //计算点的个数  起始点  和
        // Point ponit1 = mPonitsList2.get(1);
        // Point ponit2 = mPonitsList2.get(2);
//            int dis1 = (int) referPoint.distanc(mPonitsList2.get(1));
//            int dis2 = (int) referPoint.distanc(mPonitsList2.get(2));
        for (int i = 1; i < 6; i++) {
            int x = (int) (referPoint.x + i * (ponit1.x - referPoint.x) / 5);
            int y = (int) (referPoint.y + i * (ponit1.y - referPoint.y) / 5);
            Point ponit = new Point(x, y);
            dis1Lists.add(ponit);
        }
        for (int i = 1; i < 6; i++) {
            int x = (int) (referPoint.x + i * (ponit2.x - referPoint.x) / 5);
            int y = (int) (referPoint.y + i * (ponit2.y - referPoint.y) / 5);
            Point ponit = new Point(x, y);
            dis2Lists.add(ponit);
        }
        //奇数个点
        for (int i = dis2Lists.size() - 1; i >= 0; i--) {
            if (i == dis2Lists.size() - 1) {
                mPath2.moveTo(ponit1.x, ponit1.y);
                mPath2.lineTo(ponit2.x, ponit2.y);
            }
            if (i % 2 == 0) {
                mPath2.lineTo(dis1Lists.get(i).x, dis1Lists.get(i).y);
                mPath2.lineTo(dis2Lists.get(i).x, dis2Lists.get(i).y);
            }
            if (i % 2 == 1) {
                mPath2.lineTo(dis2Lists.get(i).x, dis2Lists.get(i).y);
                mPath2.lineTo(dis1Lists.get(i).x, dis1Lists.get(i).y);
            }
        }
        canvas.drawPath(mPath2, paint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isDraw) {
            return true;
        }
        int eventX = (int) event.getX();
        int eventY = (int) event.getY();
        // ▼ 注意这里使用的是 getAction()，先埋一个小尾巴。
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 手指按下
                break;
            case MotionEvent.ACTION_MOVE:
                // 手指移动
                break;
            case MotionEvent.ACTION_UP:
//                if (isDraw) {
//                    Point mPonits = new Point();
//                    if (eventX < mP1.x && eventX > mP2.x || eventY < mP1.y && eventY > mP3.y) {
//                        return false;
//                    }
//                    mPonits.setX(eventX);
//                    mPonits.setY(eventY);
//                    mPonitsList2.add(mPonits);
//                }

//                mPathline = new Path();
//                if (mPonitsList2.size() == 0) {
//                    mPathline.moveTo(mPonits.x, mPonits.y);
//                } else {
//                    mPathline.lineTo(mPonits.x, mPonits.y);
//
//                }

                // mPaths.add(mPathline);

                // 手指抬起
                break;
            case MotionEvent.ACTION_CANCEL:
                // 事件被拦截
                break;
            case MotionEvent.ACTION_OUTSIDE:
                // 超出区域
                break;
            default:
                break;
        }
        this.invalidate();
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        int action = event.getAction();
//        if (action == MotionEvent.ACTION_DOWN) {
//            oriLeft = v.getLeft();
//            oriRight = v.getRight();
//            oriTop = v.getTop();
//            oriBottom = v.getBottom();
//            lastY = (int) event.getRawY();
//            lastX = (int) event.getRawX();
//            dragDirection = getDirection(v, (int) event.getX(),
//                    (int) event.getY());
//        }
//        // 处理拖动事件
//       // delDrag(v, event, action);
//        invalidate();
        return true;
    }


    protected void delDrag(View v, MotionEvent event, int action) {
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                int dx = (int) event.getRawX() - lastX;
                int dy = (int) event.getRawY() - lastY;
                switch (dragDirection) {
                    case LEFT: // 左边缘
                        left(v, dx);
                        break;
                    case RIGHT: // 右边缘
                        right(v, dx);
                        break;
                    case BOTTOM: // 下边缘
                        bottom(v, dy);
                        break;
                    case TOP: // 上边缘
                        top(v, dy);
                        break;
                    case CENTER: // 点击中心-->>移动
                        center(v, dx, dy);
                        break;
                    case LEFT_BOTTOM: // 左下
                        left(v, dx);
                        bottom(v, dy);
                        break;
                    case LEFT_TOP: // 左上
                        left(v, dx);
                        top(v, dy);
                        break;
                    case RIGHT_BOTTOM: // 右下
                        right(v, dx);
                        bottom(v, dy);
                        break;
                    case RIGHT_TOP: // 右上
                        right(v, dx);
                        top(v, dy);
                        break;
                }
                if (dragDirection != CENTER) {
                    v.layout(oriLeft, oriTop, oriRight, oriBottom);
                }
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                dragDirection = 0;
                break;
        }
    }


    private void center(View v, int dx, int dy) {
        int left = v.getLeft() + dx;
        int top = v.getTop() + dy;
        int right = v.getRight() + dx;
        int bottom = v.getBottom() + dy;
        if (left < -offset) {
            left = -offset;
            right = left + v.getWidth();
        }
        if (right > screenWidth + offset) {
            right = screenWidth + offset;
            left = right - v.getWidth();
        }
        if (top < -offset) {
            top = -offset;
            bottom = top + v.getHeight();
        }
        if (bottom > screenHeight + offset) {
            bottom = screenHeight + offset;
            top = bottom - v.getHeight();
        }
        v.layout(left, top, right, bottom);
    }

    public void getAllEdgLinePoints() {

    }

    private void top(View v, int dy) {
        oriTop += dy;
        if (oriTop < -offset) {
            oriTop = -offset;
        }
        if (oriBottom - oriTop - 2 * offset < 200) {
            oriTop = oriBottom - 2 * offset - 200;
        }
    }


    private void bottom(View v, int dy) {
        oriBottom += dy;
        if (oriBottom > screenHeight + offset) {
            oriBottom = screenHeight + offset;
        }
        if (oriBottom - oriTop - 2 * offset < 200) {
            oriBottom = 200 + oriTop + 2 * offset;
        }
    }


    private void right(View v, int dx) {
        oriRight += dx;
        if (oriRight > screenWidth + offset) {
            oriRight = screenWidth + offset;
        }
        if (oriRight - oriLeft - 2 * offset < 200) {
            oriRight = oriLeft + 2 * offset + 200;
        }
    }


    private void left(View v, int dx) {
        oriLeft += dx;
        if (oriLeft < -offset) {
            oriLeft = -offset;
        }
        if (oriRight - oriLeft - 2 * offset < 200) {
            oriLeft = oriRight - 2 * offset - 200;
        }
    }


    protected int getDirection(View v, int x, int y) {
        int left = v.getLeft();
        int right = v.getRight();
        int bottom = v.getBottom();
        int top = v.getTop();
        if (x < 40 && y < 40) {
            return LEFT_TOP;
        }
        if (y < 40 && right - left - x < 40) {
            return RIGHT_TOP;
        }
        if (x < 40 && bottom - top - y < 40) {
            return LEFT_BOTTOM;
        }
        if (right - left - x < 40 && bottom - top - y < 40) {
            return RIGHT_BOTTOM;
        }
        if (x < 40) {
            return LEFT;
        }
        if (y < 40) {
            return TOP;
        }
        if (right - left - x < 40) {
            return RIGHT;
        }
        if (bottom - top - y < 40) {
            return BOTTOM;
        }
        return CENTER;
    }


    public int getCutWidth() {
        return getWidth() - 2 * offset;
    }


    public int getCutHeight() {
        return getHeight() - 2 * offset;
    }

    // 重置画布
    public void reset() {
        mPath.reset();
    }

    //确定
    public void setConfirmPoint() {
        mDivPath.reset();
        if (mPonitsList2 != null && mPonitsList2.size() > 0) {
            isOk = true;
            isDraw = false;
            invalidate();
        }
    }

    //清除
    public void clearPath() {
        if (mPonitsList2 != null && mPonitsList2.size() > 0) {
            mPonitsList2.clear();
            mDivPath.reset();
            mPath2.reset();
            isDraw = true;
            invalidate();
        }
    }

    public void addPoint(List<Point> ponitsList) {
        mDivPath.reset();
        this.mPonitsList2 = ponitsList;
        invalidate();

    }

    /**
     * 设置标准参考点
     * x,方向  和 y，方向
     */
    public void setStandPointTrack(String flag) {
        mFlag = flag;
        invalidate();
    }


}
