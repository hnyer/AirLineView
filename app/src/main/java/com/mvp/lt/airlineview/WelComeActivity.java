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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_);
        ButterKnife.bind(this);
        mBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelComeActivity.this,MainActivity.class));
            }
        });
        mBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelComeActivity.this,PlayMediaActivity.class));
            }
        });
    }
}
