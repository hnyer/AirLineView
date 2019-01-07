package com.mvp.lt.airlineview.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mvp.lt.airlineview.R;
import com.mvp.lt.airlineview.RenderGlActivity;
import com.mvp.lt.airlineview.TaskTest.TaskTestActivity;
import com.mvp.lt.airlineview.playMedia.PlayMediaActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.yznu.gdmapoperate.ui.activity.CustViewActivity;
import cn.yznu.gdmapoperate.ui.activity.MapMainActivity;


/**
 * $activityName
 *
 * @author LiuTao
 * @date 2018/8/6/006
 */


public class WelComeActivity extends AppCompatActivity {
    @BindView(R.id.btn_1)
    Button mBtn1;
    @BindView(R.id.btn_2)
    Button mBtn2;
    @BindView(R.id.btn_3)
    Button mBtn3;
    @BindView(R.id.btn_4)
    Button mBtn4;
    @BindView(R.id.btn_5)
    Button mBtn5;
    @BindView(R.id.custom_knife)
    Button customKnife;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_1, R.id.btn_2, R.id.btn_3,
            R.id.btn_4, R.id.btn_5, R.id.btn_6,
            R.id.btn_7, R.id.btn_8,
            R.id.custom_knife,
            R.id.simulation, R.id.ce_test})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_1:
                startActivity(new Intent(WelComeActivity.this, MainActivity.class));
                break;
            case R.id.btn_2:
                startActivity(new Intent(WelComeActivity.this, PlayMediaActivity.class));
                break;
            case R.id.btn_3:
                startActivity(new Intent(WelComeActivity.this, CCBActivity.class));
                break;
            case R.id.btn_4:
                startActivity(new Intent(WelComeActivity.this, CircleActivity.class));
                break;
            case R.id.btn_5:
                startActivity(new Intent(WelComeActivity.this, MapMainActivity.class));
                break;
            case R.id.btn_6:
                startActivity(new Intent(WelComeActivity.this, CustViewActivity.class));
                break;
            case R.id.btn_7:
                startActivity(new Intent(WelComeActivity.this, RenderGlActivity.class));
                break;
            case R.id.btn_8:
                startActivity(new Intent(WelComeActivity.this, TaskTestActivity.class));
                break;
            case R.id.custom_knife:
                startActivity(new Intent(WelComeActivity.this, TextCustomKnifeActivity.class));
                break;
            case R.id.simulation:
                startActivity(new Intent(WelComeActivity.this, com.example.clickservice.MainActivity.class));
                break;
            case R.id.ce_test:
                String str = "123";
                setString(str);
                Log.e("sdsd", str);
                break;
        }
    }


    public void setString(String str) {
        str = "abc";
    }
}
