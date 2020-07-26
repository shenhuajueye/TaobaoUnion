package com.example.taobaounion.ui.activity;

import android.os.Bundle;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taobaounion.R;
import com.example.taobaounion.ui.custom.TextFlowLayout;
import com.example.taobaounion.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestActivity extends AppCompatActivity {
    @BindView(R.id.test_navigation_bar)
    public RadioGroup navigation;
    @BindView(R.id.test_flow_text)
    public TextFlowLayout flowText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        initListener();
        List<String> textList = new ArrayList<>();
        textList.add("电脑");
        textList.add("机械键盘");
        textList.add("滑板");
        textList.add("肥宅快乐水");
        textList.add("手机");
        textList.add("显示屏");
        textList.add("耳机");
        textList.add("音箱");
        textList.add("电脑");
        textList.add("机械键盘");
        textList.add("滑板");
        textList.add("肥宅快乐水");
        textList.add("手机");
        textList.add("显示屏");
        textList.add("耳机");
        textList.add("音箱");
        flowText.setTextList(textList);
        flowText.setOnFlowTextItemClickListener(new TextFlowLayout.OnFlowTextItemClickListener() {
            @Override
            public void onFlowItemClick(String text) {
                LogUtils.d(TestActivity.this,"click text -->" + text);
            }
        });
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
