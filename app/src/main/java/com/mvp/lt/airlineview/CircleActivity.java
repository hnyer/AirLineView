package com.mvp.lt.airlineview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mvp.lt.airlineview.view.CircleMenuLayout2;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * <pre>
 * @author zhy
 * http://blog.csdn.net/lmj623565791/article/details/43131133
 * </pre>
 */
public class CircleActivity extends Activity {

    @BindView(R.id.input_value)
    EditText mInputValue;

    @BindView(R.id.input_value_ok)
    TextView mInputValueOk;
    @BindView(R.id.id_circle_menu_item_center)
    RelativeLayout mIdCircleMenuItemCenter;
    @BindView(R.id.id_menulayout)
    CircleMenuLayout2 mIdMenulayout;
    @BindView(R.id.set_seekBar)
    SeekBar mSetSeekBar;
    @BindView(R.id.radio_button1)
    RadioButton mRadioButton1;
    @BindView(R.id.radio_button2)
    RadioButton mRadioButton2;
    @BindView(R.id.radio_group)
    RadioGroup mRadioGroup;
    @BindView(R.id.id_circle_menu_item_center_img)
    ImageView mIdCircleMenuItemCenterImg;
    private CircleMenuLayout2 mCircleMenuLayout;

    private String[] mItemTexts = new String[]{"B", "C", "D", "A"};
    private int[] mItemImgs = new int[]{R.drawable.ic_right_menu,
            R.drawable.ic_bottom_menu, R.drawable.ic_left_menu,
            R.drawable.ic_top_menu};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //自已切换布局文件看效果
        setContentView(R.layout.activity_main02);
        ButterKnife.bind(this);
        mCircleMenuLayout = (CircleMenuLayout2) findViewById(R.id.id_menulayout);
        mCircleMenuLayout.setMenuItemIconsAndTexts(mItemImgs, mItemTexts);
        mSetSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mInputValue.setText(progress + "");
                mCircleMenuLayout.setStartAngle(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mCircleMenuLayout.setOnMenuItemClickListener(new CircleMenuLayout2.OnMenuItemClickListener() {

            @Override
            public void itemClick(View view, int pos) {
                Toast.makeText(CircleActivity.this, mItemTexts[pos],
                        Toast.LENGTH_SHORT).show();

            }

            @Override
            public void itemCenterClick(View view) {
                Toast.makeText(CircleActivity.this,
                        "you can do something ",
                        Toast.LENGTH_SHORT).show();

            }

            @Override
            public void itemAngle(double angle) {
                if (angle > 360) {
                    angle = angle - 360;
                } else if (angle == 360) {
                    angle = 0;
                }
                mInputValue.setText((int) angle + "");

            }
        });
        mInputValueOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCircleMenuLayout.setStartAngle(Integer.parseInt(mInputValue.getText().toString()));
            }
        });

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.radio_button1:
                        mCircleMenuLayout.setEnableScroll(true);
                        break;
                    case R.id.radio_button2:
                        mCircleMenuLayout.setEnableScroll(false);
                        break;
                }
            }
        });

    }

}
