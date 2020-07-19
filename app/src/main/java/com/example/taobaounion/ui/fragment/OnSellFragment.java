package com.example.taobaounion.ui.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseFragment;
import com.example.taobaounion.model.domain.IBaseInfo;
import com.example.taobaounion.model.domain.OnSellContent;
import com.example.taobaounion.presenter.IOnSellPagePresenter;
import com.example.taobaounion.presenter.ITicketPresenter;
import com.example.taobaounion.ui.activity.TicketActivity;
import com.example.taobaounion.ui.adapter.OnSellPageContentAdapter;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.PresenterManager;
import com.example.taobaounion.utils.SizeUtils;
import com.example.taobaounion.utils.TicketUtil;
import com.example.taobaounion.utils.ToastUtils;
import com.example.taobaounion.view.IOnSellPageCallback;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import butterknife.BindView;

public class OnSellFragment extends BaseFragment implements IOnSellPageCallback, OnSellPageContentAdapter.OnSellPageItemClickListener {

    private IOnSellPagePresenter onSellPagePresenter;

    public static final int DEFAULT_SPAN_COUNT = 2;

    @BindView(R.id.on_sell_content_list)
    public RecyclerView onSellContentList;

    @BindView(R.id.on_sell_refresh_layout)
    public TwinklingRefreshLayout refreshLayout;
    @BindView(R.id.fragment_bar_title_tv)
    public TextView barTitleTv;


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
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_with_bar_layout,container,false);
    }

    @Override
    protected void initView(View rootView) {

        onSellPageContentAdapter = new OnSellPageContentAdapter();
        //设置布局管理器
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),DEFAULT_SPAN_COUNT);
        onSellContentList.setLayoutManager(gridLayoutManager);
        onSellContentList.setAdapter(onSellPageContentAdapter);
        onSellContentList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = SizeUtils.dip2px(getContext(),2.5f);
                outRect.bottom = SizeUtils.dip2px(getContext(),2.5f);
                outRect.left = SizeUtils.dip2px(getContext(),2.5f);
                outRect.right = SizeUtils.dip2px(getContext(),2.5f);
            }
        });
        refreshLayout.setEnableLoadmore(true);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableOverScroll(true);
        barTitleTv.setText("特惠宝贝");
    }

    @Override
    protected void initListener() {
        super.initListener();
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                //去加载更多内容
                if (onSellPagePresenter != null) {
                    onSellPagePresenter.loaderMore();
                }
            }
        });

        onSellPageContentAdapter.setOnSellPageItemClickListener(this);
    }

    @Override
    public void onContentLoadedSuccess(OnSellContent result) {
        //数据回来了
        setUpState(State.SUCCESS);
        //更新UI
        onSellPageContentAdapter.setData(result);
    }

    @Override
    public void onMoreLoaded(OnSellContent moreResult) {
        refreshLayout.finishLoadmore();
        //添加内容到适配器
        onSellPageContentAdapter.onMoreLoaded(moreResult);
    }

    @Override
    public void onMoreLoadedError() {
        refreshLayout.finishLoadmore();
        ToastUtils.showToast("网络异常，请稍后重试。");
    }

    @Override
    public void onMoreLoadedEmpty() {
        refreshLayout.finishLoadmore();
        ToastUtils.showToast("没有更多内容。。。");
    }

    @Override
    public void onError() {
        setUpState(State.ERROR);
    }

    @Override
    public void onLoading() {
        setUpState(State.LOADING);
    }

    @Override
    public void onEmpty() {
        setUpState(State.EMPTY);
    }

    @Override
    public void onSellItemClick(IBaseInfo item) {
        TicketUtil.toTicketPage(getContext(),item);
    }

}
