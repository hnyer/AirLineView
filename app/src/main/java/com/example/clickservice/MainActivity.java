package com.example.clickservice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mvp.lt.airlineview.App;
import com.mvp.lt.airlineview.R;
import com.mvp.lt.airlineview.utils.ToastUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends Activity implements OnClickListener {

    int width, x;
    int height, y;
    private TextView txt, location_tip;
    private EditText txt_edit;
    private Button start, stop, scrollview, setting, location_btn;
    private int distance;
    private Process mProcess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click);
        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();
        distance = 1000;
        x = 221;
        y = 1857;
        Log.d("width，height", width + "," + height);
        txt = (TextView) findViewById(R.id.tiptxt);
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.end);
        setting = (Button) findViewById(R.id.txt_setting);
        txt_edit = (EditText) findViewById(R.id.txt_canshu);
        scrollview = (Button) findViewById(R.id.scrollview);
        location_btn = (Button) findViewById(R.id.location_btn);
        location_tip = (TextView) findViewById(R.id.location_tip);
        Button shootScreen = (Button) findViewById(R.id.shootScreen);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        scrollview.setOnClickListener(this);
        setting.setOnClickListener(this);
        location_btn.setOnClickListener(this);
        txt_edit.setText("1000");
        //new ThreadClass(width,height).start();
        shootScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast("截图");
                //
//                String Ss = "adb shell screencap -p storage/emulated/0/shootScreen/screen.png";
                String Ss = "adb shell screencap -p" + getRootDir() + "/screen.png";
                shotScreenPBitmap(Ss);
                String Ss2 = "adb pull " + getRootDir() + "/screen.png";
                shotScreenPBitmap(Ss2);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        x = (int) event.getX();
        y = (int) event.getY();
        location_tip.setText("你点击的屏幕位置为:" + x + "," + y);
        return super.onTouchEvent(event);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        int id = arg0.getId();
        switch (id) {
            case R.id.start:
                //Toast.makeText(this, "你点击了停止按钮", 1000).show();
                startFloatingButtonService();
                break;
            case R.id.end:
                //Toast.makeText(this, "你点击了开始按钮", 1000).show();
                Intent stopIntent = new Intent(this, BackGroundService.class);
                stopService(stopIntent);
                //unbindService(stopIntent);
                break;
            case R.id.scrollview:
                Intent scrollactivity = new Intent(this, myScrollView.class);
                startActivity(scrollactivity);
            case R.id.txt_setting:
                distance = Integer.parseInt(txt_edit.getText().toString());
                txt.setText("设置刷新间隔为:" + distance + "豪秒");
                break;
            case R.id.location_btn:
                Toast.makeText(this, "设置成功", 1500).show();
                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                Intent startIntent = new Intent(this, BackGroundService.class);
                startIntent.putExtra("distance", distance);
                startIntent.putExtra("x", x);
                startIntent.putExtra("y", y);
                startService(startIntent);
            }
        } else if (requestCode == 1) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                Intent startIntent = new Intent(this, BackGroundService.class);
                startIntent.putExtra("distance", distance);
                startIntent.putExtra("x", x);
                startIntent.putExtra("y", y);
                startService(startIntent);
            }
        } else if (requestCode == 2) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                Intent startIntent = new Intent(this, BackGroundService.class);
                startIntent.putExtra("distance", distance);
                startIntent.putExtra("x", x);
                startIntent.putExtra("y", y);
                startService(startIntent);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startFloatingButtonService() {
        if (BackGroundService.isStarted) {
            return;
        }
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT);
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
        } else {
            Intent startIntent = new Intent(this, BackGroundService.class);
            startIntent.putExtra("distance", distance);
            startIntent.putExtra("x", x);
            startIntent.putExtra("y", y);
            startService(startIntent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startFloatingImageDisplayService() {
        if (BackGroundService.isStarted) {
            return;
        }
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT);
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 1);
        } else {
            Intent startIntent = new Intent(this, BackGroundService.class);
            startIntent.putExtra("distance", distance);
            startIntent.putExtra("x", x);
            startIntent.putExtra("y", y);
            startService(startIntent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startFloatingVideoService() {
        if (BackGroundService.isStarted) {
            return;
        }
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT);
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 2);
        } else {
            Intent startIntent = new Intent(this, BackGroundService.class);
            startIntent.putExtra("distance", distance);
            startIntent.putExtra("x", x);
            startIntent.putExtra("y", y);
            startService(startIntent);
        }
    }

    /**
     * 截图
     */
    public void shotScreenPBitmap(String cmd) {
        try {
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
            Log.d("点击事件", "点击事件执行截图");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断SD卡是否挂载
     */
    public static boolean isSDCardAvailable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 文件目录
     * 获取应用目录，当SD卡存在时，获取SD卡上的目录，当SD卡不存在时，获取应用的cache目录
     * 这得到的路径为 /a/b/c/point
     */
    public static String getRootDir() {
        StringBuilder sb = new StringBuilder();
        if (isSDCardAvailable()) {
            sb.append(getExternalStoragePath());

        } else {
            sb.append(getCachePath());
        }

        String path = sb.toString();
        if (createDirs(path)) {
            return path;
        } else {
            return "";
        }
    }

    /**
     * 创建文件夹
     */
    public static boolean createDirs(String dirPath) {
        File file = new File(dirPath);
        Log.e("TAG", "filepath:" + file);
        if (!file.exists() || !file.isDirectory()) {
            return file.mkdirs();
        }
        return true;
    }

    /**
     * 获取应用的cache目录
     */
    public static String getCachePath() {
        File f = App.getInstance().getCacheDir();
        if (null == f) {
            return null;
        } else {
            return f.getAbsolutePath() + "/";
        }
    }

    /**
     * 获取SD下的应用目录
     */
    public static String getExternalStoragePath() {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb.append(File.separator);
        sb.append("shootScreen");
        sb.append(File.separator);
        return sb.toString();
    }
}
