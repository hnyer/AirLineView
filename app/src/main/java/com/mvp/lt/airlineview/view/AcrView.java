package com.mvp.lt.airlineview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * $activityName
 *
 * @author LiuTao
 * @date 2018/10/6/006
 */


public class AcrView extends View {
    public AcrView(Context context) {
        super(context);
    }

    public AcrView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AcrView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    Path path = new Path(); // 初始化 Path 对象

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.BLACK);
        path.addCircle(200, 200, 100, Path.Direction.CCW);
        canvas.drawPath(path, paint);
    }
}
