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


public class RenderGlActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {
    @BindView(R.id.genderView)
    GLSurfaceView mGenderView;
    @BindView(R.id.add)
    Button mAdd;
    @BindView(R.id.reduce)
    Button mReduce;
    private boolean supportsEs2;
    private Handler mHandler;

    private TestRenderer mJiasudu;
    private GestureDetector mGestureDetector;
    private float tatio;
    private static final int DISTANCE = 50;
    private static final int VELOCITY = 0;
    private  double nLenStart = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(Looper.getMainLooper());
        setContentView(R.layout.render_gl_activity);
        ButterKnife.bind(this);
        mGestureDetector = new GestureDetector(this);

        mJiasudu = new TestRenderer(mHandler);
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
        Log.i("MotionEvent","onShowPress");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.i("MotionEvent","onSingleTapUp");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.i("MotionEvent","onScroll"+(e1.getX()-e2.getX()));
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.i("MotionEvent","onLongPress");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }



//    //手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
//    float x1 = 0;
//    float x2 = 0;
//    float y1 = 0;
//    float y2 = 0;
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        //继承了Activity的onTouchEvent方法，直接监听点击事件
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            //当手指按下的时候
//            x1 = event.getX();
//            y1 = event.getY();
//        }
//        if (event.getAction() == MotionEvent.ACTION_MOVE) {
//            x2 = event.getX();
//            y2 = event.getY();
//            if (x1 - x2 > 50) {
//                mJiasudu.glRotate((x1 - x2) - 50, -2f);
////                Toast.makeText(MediaPlayBackActivity.this, "向左滑", Toast.LENGTH_SHORT).show();
//            } else if (x2 - x1 > 50) {
////                Toast.makeText(MediaPlayBackActivity.this, "向右滑", Toast.LENGTH_SHORT).show();
//                mJiasudu.glRotate((x2 - x1) - 50, 2f);
//            } else if (y1 - y2 > 50) {
//
////                Toast.makeText(MediaPlayBackActivity.this, "向上滑", Toast.LENGTH_SHORT).show();
//            } else if (y2 - y1 > 50) {
//                // Toast.makeText(MediaPlayBackActivity.this, "向下滑", Toast.LENGTH_SHORT).show();
//            }
//        }
//        if (event.getAction() == MotionEvent.ACTION_UP) {
//            //当手指离开的时候
//
//
//        }
//        return super.onTouchEvent(event);
//    }
}
