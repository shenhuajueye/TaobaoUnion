package com.example.taobaounion.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.taobaounion.R;
import com.example.taobaounion.utils.LogUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {

    private State currentState = State.NONE;
    private View successView;
    private View loadingView;
    private View errorView;
    private View emptyView;

    public enum State {
        NONE, LOADING, SUCCESS, ERROR, EMPTY
    }

    private Unbinder bind;
    private FrameLayout baseContainer;

    @OnClick(R.id.network_error_tips)
    public void retry(){
        //点击了重新加载内容
        LogUtils.d(this,"on retry...");
        onRetryClick();
    }

    /**
     * 如果子类fragment需要知道网络错误的点击，那重写方法覆盖即可
     */
    protected void onRetryClick() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = loadRootView(inflater,container);
        baseContainer = (FrameLayout) rootView.findViewById(R.id.base_container);
        loadStatesView(inflater, container);
        bind = ButterKnife.bind(this, rootView);
        initView(rootView);
        initListener();
        initPresenter();
        loadData();
        return rootView;
    }

    /**
     * 如果子类需要去设置相关的监听事件，可覆盖此方法
     */
    protected void initListener() {

    }

    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.base_fragment_layout, container, false);
    }

    /**
     * 加载各种状态的View
     *
     * @param inflater
     * @param container
     */
    protected void loadStatesView(LayoutInflater inflater, ViewGroup container) {
        //成功的View
        successView = loadSuccessView(inflater, container);
        baseContainer.addView(successView);

        //Loading的View
        loadingView = loadLoadingView(inflater, container);
        baseContainer.addView(loadingView);

        //错误界面
        errorView = loadErrorView(inflater, container);
        baseContainer.addView(errorView);
        //空界面
        emptyView = loadEmptyView(inflater, container);
        baseContainer.addView(emptyView);

        setUpState(State.NONE);
    }

    protected View loadSuccessView(LayoutInflater inflater, ViewGroup container) {
        int resId = getRootViewResId();
        return inflater.inflate(resId, container, false);
    }

    /**
     * 加载loading界面
     *
     * @param inflater
     * @param container
     * @return
     */
    protected View loadLoadingView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_loading, container, false);
    }

    private View loadErrorView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_error, container, false);
    }

    private View loadEmptyView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_empty, container, false);
    }


    /**
     * 子类通过状态来切换页面即可
     *
     * @param state
     */
    public void setUpState(State state) {
        this.currentState = state;
        successView.setVisibility(currentState == State.SUCCESS ? View.VISIBLE : View.GONE);
        loadingView.setVisibility(currentState == State.LOADING ? View.VISIBLE : View.GONE);
        errorView.setVisibility(currentState == State.ERROR ? View.VISIBLE : View.GONE);
        emptyView.setVisibility(currentState == State.EMPTY ? View.VISIBLE : View.GONE);
    }


    protected void initView(View rootView) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bind != null) {
            bind.unbind();
        }
        release();
    }

    protected void release() {
        //释放资源

    }

    protected void initPresenter() {
        //创建Presenter
    }

    protected void loadData() {

        //加载数据
    }


    protected abstract int getRootViewResId();
}
