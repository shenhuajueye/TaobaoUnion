package com.example.taobaounion.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.example.taobaounion.R;


/**
 * 功能：自动轮播
 */
public class AutoLooperViewPager extends ViewPager {

    //切换间隔时长，单位毫秒
    public static final long DEFAULT_DURATION = 3000;
    private long mDuration = DEFAULT_DURATION;

    public AutoLooperViewPager(@NonNull Context context) {
        this(context, null);
    }

    public AutoLooperViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //读取属性
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AutoLooperStyle);
        //获取属性
        mDuration = typedArray.getInteger(R.styleable.AutoLooperStyle_duration, (int) DEFAULT_DURATION);
        //回收
        typedArray.recycle();
    }

    private boolean isLooper = false;

    public void startLooper() {
        isLooper = true;
        //先拿到当前的位置
        post(mTask);
    }

    /**
     * 设置切换时长
     *
     * @param duration 时长，单位是毫秒
     */
    private void setDuration(long duration) {
        this.mDuration = duration;
    }

    private Runnable mTask = new Runnable() {
        @Override
        public void run() {
            int currentItem = getCurrentItem();
            currentItem++;
            setCurrentItem(currentItem);
            if (isLooper) {
                postDelayed(this, mDuration);
            }
        }
    };

    public void stopLooper() {
        isLooper = false;
        removeCallbacks(mTask);
    }
}
