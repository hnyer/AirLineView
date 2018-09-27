package com.mvp.lt.airlineview;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4, R.id.btn_5,R.id.btn_6})
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
        }
    }
}
