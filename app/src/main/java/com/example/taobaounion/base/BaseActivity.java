package com.example.taobaounion.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {
    private Unbinder bind;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        bind = ButterKnife.bind(this);
        initView();
        initEvent();
        initPresenter();
    }

    protected abstract void initPresenter();

    /**
     * 需要时复写
     */
    protected void initEvent() {
    }

    protected abstract void initView();

    protected abstract int getLayoutResId();
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind != null) {
            bind.unbind();
        }
        this.release();
    }

    /**
     * 子类需要复写该方法
     */
    protected void release() {

    }

}
