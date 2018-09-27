package com.mvp.lt.airlineview.utils;

import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.mvp.lt.airlineview.App;


/**
 * * @author ${LiuTao}
 *
 * @date 2018/3/17/017
 */

public class ToastUtils {
    private static Toast toast;
    private static Handler mUIHandler = new Handler(Looper.getMainLooper());

    public static void showToast(final String msg) {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(App.getInstance(), msg + "", Toast.LENGTH_LONG);
                }
                toast.setText(msg);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

    public static void showToast(final int resId) {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(App.getInstance(), resId, Toast.LENGTH_LONG);
                }
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.setText(resId);
                toast.show();
            }
        });
    }


    public static void setResultToToast(final String string) {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(App.getInstance(), string + "", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void setResultToText(final TextView tv, final String s) {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                if (tv == null) {
                    // Toast.makeText(FPVDemoApplication.context, "tv is null", Toast.LENGTH_LONG).show();
                } else {
                    tv.setText(s);
                }
            }
        });
    }
    /**
     * Toast 替代方法 ：立即显示无需等待
     */
    private static Toast mToast;
    private static long mExitTime;


    public static boolean doubleClickExit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            ToastUtils.showToast("再按一次退出");
            mExitTime = System.currentTimeMillis();
            return false;
        }
        return true;
    }
}
