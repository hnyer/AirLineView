package com.example.clickservice;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.mvp.lt.airlineview.R;
import com.mvp.lt.airlineview.utils.ToastUtils;

public class BackGroundService extends Service {
    public static boolean isStarted = false;
    public static final String TAG = "BackGroundService";
    ThreadAdbshell thshell;
    int distance;
    private boolean isRun = false;

    //创建服务时调用
    @Override
    public void onCreate() {
        super.onCreate();
        isStarted = true;
        Log.d(TAG, "onCreate");
        isRun = false;
        thshell = new ThreadAdbshell(90, 185, isRun);
        distance = 1000;

        sdosod();
    }

    //服务执行的操作
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        distance = intent.getIntExtra("distance", 1000);
        thshell.setDistance(distance);
        thshell.setX(intent.getIntExtra("x", 90));
        thshell.setY(intent.getIntExtra("y", 185));
        if (!thshell.isAlive()) {
            Log.d("线程是否正在执行", thshell.isAlive() + "");
            thshell.start();

        }
        showFloatingWindow();
        return super.onStartCommand(intent, flags, startId);
    }

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    public void sdosod() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = 150;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.x = 300;
        layoutParams.y = 300;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showFloatingWindow() {
        if (Settings.canDrawOverlays(this)) {

            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.alert_window_menu, null);

            TextView bottun_1 = view.findViewById(R.id.bottun_1);
            TextView bottun_2 = view.findViewById(R.id.bottun_2);
            bottun_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtils.showToast("开始");
                    if (!isRun) {
                        isRun = true;
                        thshell.setRuning(isRun);
                    }
                }
            });

            bottun_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtils.showToast("暂停");
                    if (isRun) {
                        isRun = false;
                    }
                    thshell.setRuning(isRun);

                }
            });
            windowManager.addView(view, layoutParams);
            view.setOnTouchListener(new FloatingOnTouchListener());
        }
    }

    //销毁服务时调用
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    //bindService()启动Service时才会调用onBind()方法
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;
                    windowManager.updateViewLayout(view, layoutParams);
                    break;
                default:
                    break;
            }
            return false;
        }
    }
}
