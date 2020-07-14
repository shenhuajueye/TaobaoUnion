package com.example.taobaounion.ui.fragment;

import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseFragment;
import com.example.taobaounion.model.domain.OnSellContent;
import com.example.taobaounion.presenter.IOnSellPagePresenter;
import com.example.taobaounion.ui.adapter.OnSellPageContentAdapter;
import com.example.taobaounion.utils.PresenterManager;
import com.example.taobaounion.view.IOnSellPageCallback;

import butterknife.BindView;

public class OnSellFragment extends BaseFragment implements IOnSellPageCallback {

    private IOnSellPagePresenter onSellPagePresenter;

    public static final int DEFAULT_SPAN_COUNT = 2;

    @BindView(R.id.on_sell_content_list)
    public RecyclerView onSellContentList;
    private OnSellPageContentAdapter onSellPageContentAdapter;

    @Override
    protected void initPresenter() {
        super.initPresenter();
        onSellPagePresenter = PresenterManager.getInstance().getOnSellPagePresenter();
        onSellPagePresenter.registerViewCallback(this);
        onSellPagePresenter.getOnSellContent();
    }

    @Override
    protected void release() {
        super.release();
        if (onSellPagePresenter != null) {
            onSellPagePresenter.unregisterViewCallback(this);
        }
    }

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_on_sell;
    }

    @Override
    protected void initView(View rootView) {
        setUpState(State.SUCCESS);
        onSellPageContentAdapter = new OnSellPageContentAdapter();
        //设置布局管理器
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),DEFAULT_SPAN_COUNT);
        onSellContentList.setLayoutManager(gridLayoutManager);
        onSellContentList.setAdapter(onSellPageContentAdapter);
    }

    @Override
    public void onContentLoadedSuccess(OnSellContent result) {
        //数据回来了
        //TODO:更新UI
    }

    @Override
    public void onMoreLoaded(OnSellContent moreResult) {

    }

    @Override
    public void onMoreLoadedError() {

    }

    @Override
    public void onMoreLoadedEmpty() {

    }

    @Override
    public void onError() {

    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onEmpty() {

    }
}
