package com.mvp.lt.airlineview;

import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mvp.lt.airlineview.opengles.AbstractPRenderer;
import com.mvp.lt.airlineview.opengles.TestRenderer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * $activityName
 *
 * @author LiuTao
 * @date 2018/12/26/026
 */


public class RenderGlActivity extends AppCompatActivity implements GestureDetector.OnGestureListener, View.OnTouchListener {
    @BindView(R.id.genderView)
    GLSurfaceView mGenderView;
    @BindView(R.id.add)
    Button mAdd;
    @BindView(R.id.reduce)
    Button mReduce;
    private boolean supportsEs2;
    private Handler mHandler;
    private AbstractPRenderer render;
    private TestRenderer mJiasudu;
    private GestureDetector mGestureDetector;
    private float tatio;
    private static final int DISTANCE = 50;
    private static final int VELOCITY = 0;
    private double nLenStart = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(Looper.getMainLooper());
        setContentView(R.layout.render_gl_activity);
        ButterKnife.bind(this);
        mGestureDetector = new GestureDetector(this);

        render = new TestRenderer(mHandler);
        mGenderView.setOnTouchListener(this);
        mGenderView.setRenderer(mJiasudu);
        mGenderView.setFocusable(true);
        mGenderView.setClickable(true);
        mGenderView.setLongClickable(true);

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };

    private void checkSupported() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        supportsEs2 = configurationInfo.reqGlEsVersion >= 0x2000;

        boolean isEmulator = Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                && (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86"));

        supportsEs2 = supportsEs2 || isEmulator;
    }


    @OnClick({R.id.add, R.id.reduce})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.add:
                mJiasudu.scale(1.01f, 1.01f, 1.01f);
                break;
            case R.id.reduce:
                mJiasudu.scale(0.99f, 0.99f, 0.99f);
                break;
        }
    }


    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.i("MotionEvent", "onShowPress");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.i("MotionEvent", "onSingleTapUp");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.i("MotionEvent", "onScroll" + (e1.getX() - e2.getX()));
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.i("MotionEvent", "onLongPress");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float step = 5f;
        if (e1.getX() - e2.getX() > DISTANCE && Math.abs(velocityX) > VELOCITY) {
            render.yrotate = render.yrotate + step;
            Log.i("render", "" + render.yrotate);
        } else if (e2.getX() - e1.getX() > DISTANCE && Math.abs(velocityX) > VELOCITY) {
            render.yrotate = render.yrotate - step;
        } else if (e1.getY() - e2.getY() > DISTANCE && Math.abs(velocityY) > VELOCITY) {
            render.xrotate = render.xrotate - step;
        } else if (e2.getY() - e1.getY() > DISTANCE && Math.abs(velocityY) > VELOCITY) {
            render.xrotate = render.xrotate + step;
        }
        Log.i("MotionEvent", "" + (e1.getX() - e2.getX()));
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //手势是渲染图变大小
        float scale = 0.05f;
        int nCnt = event.getPointerCount();
        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN && 2 == nCnt) {
            int xlen = Math.abs((int) event.getX(0) - (int) event.getX(1));
            int ylen = Math.abs((int) event.getY(0) - (int) event.getY(1));


            nLenStart = Math.sqrt((double) xlen * xlen + (double) ylen * ylen);
        } else if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP && 2 == nCnt) {
            int xlen = Math.abs((int) event.getX(0) - (int) event.getX(1));
            int ylen = Math.abs((int) event.getY(0) - (int) event.getY(1));
            double nLenEnd = Math.sqrt((double) xlen * xlen + (double) ylen * ylen);
            //通过两个手指开始距离和结束距离，来判断放大缩小
            if (nLenEnd > nLenStart) {
                render.XScalef = render.XScalef + scale;
                render.YScalef = render.YScalef + scale;
                render.ZScalef = render.ZScalef + scale;
                Toast.makeText(getApplicationContext(), "放大", Toast.LENGTH_LONG).show();
            } else {
                render.XScalef = render.XScalef - scale;
                render.YScalef = render.YScalef - scale;
                render.ZScalef = render.ZScalef - scale;
                Toast.makeText(getApplicationContext(), "缩小", Toast.LENGTH_LONG).show();
            }
        }
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
