package cn.yznu.gdmapoperate.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.mvp.lt.airlineview.R;
import com.mvp.lt.airlineview.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.yznu.gdmapoperate.ui.widget.GenderSwitchView;

/**
 * $activityName
 *
 * @author LiuTao
 * @date 2018/9/26/026
 */


public class CustViewActivity extends AppCompatActivity {
    @BindView(R.id.genderView)
    GenderSwitchView mGenderView;
    @BindView(R.id.show_text)
    TextView showText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);
        ButterKnife.bind(this);
        Log.e("", "=----------onCreate-----------");
        mGenderView.setSetOnSelectedListener(new GenderSwitchView.setOnSelectedListener() {
            @Override
            public void onSelectedOne() {
                Log.e("", "女士");
                ToastUtils.showToast("女士");
                showText.setText("女士");
            }

            @Override
            public void onSelectedTwo() {
                Log.e("", "男士");
                ToastUtils.showToast("男士");
                showText.setText("男士");
            }
        });
    }
}
