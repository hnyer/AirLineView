package com.example.clickservice;

import android.graphics.Point;
import android.util.Log;

import com.mvp.lt.airlineview.utils.ToastUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ThreadAdbshell extends Thread {
    // 申请获取root权限，这一步很重要，不然会没有作用
    public int start = 1;
    int x, y;
    int distance;//刷新间隔，单位毫秒
    private boolean runing = false;
    private Process mProcess;

    public ThreadAdbshell(int x, int y, boolean runing) {
        this.x = x;
        this.y = y;
        this.runing = runing;
        distance = 1000;
    }

    public void setRuning(boolean runing) {
        this.runing = runing;
    }

    private int idcount = 0;

    @Override
    public void run() {
        while (true) {
            if (runing) {
                if (start == 1) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Thread.sleep(distance);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                Log.d("点击位置：", x + "," + y);
                start++;
                //String str="input tap 252 252";
                String str = "input tap " + x + " " + y;
                try {
                    excuteTapShellCMD(str);
                    //excuteSwipCMD(str);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    idcount++;
                    ToastUtils.showToast("模拟点击失败，尝试点击第" + idcount + "次");
                }
            }
        }

    }

    /**
     * 模拟点击
     *
     * @param cmd
     * @throws IOException
     */
    public void excuteTapShellCMD(String cmd) throws IOException {
        // 申请获取root权限，这一步很重要，不然会没有作用
        mProcess = Runtime.getRuntime().exec("su");
        //获取输入流
        OutputStream outputStream = mProcess.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(
                outputStream);
        dataOutputStream.writeBytes(cmd);
        dataOutputStream.flush();
        dataOutputStream.close();
        outputStream.close();
        Log.d("点击事件", "点击事件执行");
    }

    private Point startPoint;
    private Point endPoint;

    /**
     * 模拟滑动事件
     *
     * @param cmd
     * @throws IOException
     */
    public void excuteSwipCMD(String cmd) throws IOException {
        startPoint = new Point();
        endPoint = new Point();
        // 申请获取root权限，这一步很重要，不然会没有作用
        mProcess = Runtime.getRuntime().exec("su");
        startPoint.set(250, 250);
        endPoint.set(500, 250);
        cmd = "input swipe  " + startPoint.x + " " + startPoint.y + " " + endPoint.x + " " + endPoint.y;
        OutputStream outputStream = mProcess.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(
                outputStream);
        dataOutputStream.writeBytes(cmd);
        dataOutputStream.flush();
        dataOutputStream.close();
        outputStream.close();
        Log.d("滑动事件", "滑动事件执行");
    }

    public void setDistance(int dis) {
        this.distance = dis;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {

        this.y = y;
    }

    private OutputStream os;

    /**
     * 执行ADB命令： input tap 125 340
     */
    private final void exec(String cmd) {
        try {
            if (os == null) {
                os = Runtime.getRuntime().exec("su").getOutputStream();
            }
            os.write(cmd.getBytes());
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("GK", e.getMessage());
        }
    }
}
