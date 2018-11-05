package com.mvp.lt.airlineview.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.mvp.lt.airlineview.R;
import com.mvp.lt.airlineview.annotation.customBuf.InjectView;
import com.mvp.lt.airlineview.annotation.customBuf.InjectViewUtils;

/**
 * $activityName
 *
 * @author LiuTao
 * @date 2018/11/5/005
 */


public class TextCustomKnifeActivity extends AppCompatActivity {
    @InjectView(id = R.id.login)
    private TextView login;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        InjectViewUtils.parse(this);
        login.setText("登录成功");
    }
}
