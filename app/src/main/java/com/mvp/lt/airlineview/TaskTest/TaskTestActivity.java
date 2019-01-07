package com.mvp.lt.airlineview.TaskTest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.mvp.lt.airlineview.R;
import com.mvp.lt.airlineview.TimerTask.base.TaskPriority;


/**
 * $activityName
 *
 * @author LiuTao
 * @date 2019/1/5/005
 */


public class TaskTestActivity extends AppCompatActivity {

    private TextView mShowTask;
    private StringBuffer mStringBuffer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_test);
        mShowTask = findViewById(R.id.show_task);
        mStringBuffer = new StringBuffer();
    }

    public void low(View view) {
        new LogTask(this, "LOW")
                .setDuration(5000)
                .setPriority(TaskPriority.LOW)
                .enqueue();
    }

    public void medial(View view) {
        new LogTask(this, "DEFOUT")
                .setDuration(4000)
                .setPriority(TaskPriority.DEFAULT)
                .enqueue();
    }

    public void hight(View view) {
        new LogTask(this, "HIGHT")
                .setDuration(3000)
                .setPriority(TaskPriority.HIGH)
                .enqueue();
    }

    public void updateShowText(String s) {
        if (mStringBuffer.length() > 1000) {
            mStringBuffer.delete(0, mStringBuffer.length());
        }
        mStringBuffer.append(s).append("\n");
        mShowTask.setText(mStringBuffer.toString());
    }
}
