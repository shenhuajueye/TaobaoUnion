package com.example.taobaounion.base;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taobaounion.utils.LogUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {
    private Unbinder bind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        //设置APP应用为清明节的灰色主题
        //=======================================================
        //View decorView = this.getWindow().getDecorView();
        //ColorMatrix cm = new ColorMatrix();
        //cm.setSaturation(0);
        //Paint paint = new Paint();
        //paint.setColorFilter(new ColorMatrixColorFilter(cm));
        //decorView.setLayerType(View.LAYER_TYPE_SOFTWARE,paint);
        //========================================================
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
