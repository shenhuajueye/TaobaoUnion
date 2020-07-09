package com.example.taobaounion.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taobaounion.R;
import com.example.taobaounion.utils.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestActivity extends AppCompatActivity {
    @BindView(R.id.test_navigation_bar)
    public RadioGroup navigation;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        initListener();
    }

    private void initListener() {
        navigation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                LogUtils.d(this,"checkId --> " + i);
                switch (i){
                    case R.id.test_home:
                        LogUtils.d(this,"切换到首页");
                        break;
                    case R.id.test_selected:
                        LogUtils.d(this,"切换到精选");
                        break;
                    case R.id.test_red_packet:
                        LogUtils.d(this,"切换到特惠");
                        break;
                    case R.id.test_search:
                        LogUtils.d(this,"切换到搜索");
                        break;
                }
            }
        });
    }
}
