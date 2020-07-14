package com.example.taobaounion.ui.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseFragment;
import com.example.taobaounion.model.domain.SelectedContent;
import com.example.taobaounion.model.domain.SelectedPageCategory;
import com.example.taobaounion.presenter.ISelectedPagePresenter;
import com.example.taobaounion.presenter.ITicketPresenter;
import com.example.taobaounion.ui.activity.TicketActivity;
import com.example.taobaounion.ui.adapter.SelectedPageContentAdapter;
import com.example.taobaounion.ui.adapter.SelectedPageLeftAdapter;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.PresenterManager;
import com.example.taobaounion.utils.SizeUtils;
import com.example.taobaounion.view.ISelectedPageCallback;

import java.util.List;

import butterknife.BindView;

public class SelectedFragment extends BaseFragment implements ISelectedPageCallback, SelectedPageLeftAdapter.IOnLeftItemClickListener, SelectedPageContentAdapter.OnSelectedPageContentItemClickListener {

    @BindView(R.id.left_category_list)
    public RecyclerView leftCategoryList;

    @BindView(R.id.right_content_list)
    public RecyclerView rightContentList;

    private ISelectedPagePresenter selectedPagePresenter;
    private SelectedPageLeftAdapter leftAdapter;
    private SelectedPageContentAdapter rightAdapter;

    @Override
    protected void initPresenter() {
        super.initPresenter();
        selectedPagePresenter = PresenterManager.getInstance().getSelectedPagePresenter();
        selectedPagePresenter.registerViewCallback(this);
        selectedPagePresenter.getCategories();
    }

    @Override
    protected void onRetryClick() {
        //重试
        if (selectedPagePresenter != null) {
            selectedPagePresenter.reloadContent();
        }
    }

    @Override
    protected void release() {
        super.release();
        selectedPagePresenter.unregisterViewCallback(this);
    }

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_selected;
    }

    @Override
    protected void initView(View rootView) {
        setUpState(State.SUCCESS);
        leftCategoryList.setLayoutManager(new LinearLayoutManager(getContext()));
        leftAdapter = new SelectedPageLeftAdapter();
        leftCategoryList.setAdapter(leftAdapter);

        rightContentList.setLayoutManager(new LinearLayoutManager(getContext()));
        rightAdapter = new SelectedPageContentAdapter();
        rightContentList.setAdapter(rightAdapter);
        rightContentList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int topAndBottom = SizeUtils.dip2px(getContext(),4);
                int leftAndRight = SizeUtils.dip2px(getContext(),6);
                outRect.top = topAndBottom;
                outRect.bottom = topAndBottom;
                outRect.left = leftAndRight;
                outRect.right = leftAndRight;

            }
        });
    }

    @Override
    protected void initListener() {
        super.initListener();
        leftAdapter.setOnLeftItemClickListener(this);
        rightAdapter.setOnSelectedPageContentItemClickListener(this);
    }

    @Override
    public void onCategoriesLoaded(SelectedPageCategory categories) {
        setUpState(State.SUCCESS);
        leftAdapter.setData(categories);
        //分类内容
        //LogUtils.d(this,"onCategoriesLoaded -->" + categories.toString());
        //TODO:更新UI
        //根据当前选中的分类，获取分类详情内容
       // List<SelectedPageCategory.DataBean> data = categories.getData();
        //selectedPagePresenter.getContentByCategory(data.get(0));

    }

    @Override
    public void onContentLoaded(SelectedContent content) {
        rightAdapter.setData(content);
        rightContentList.scrollToPosition(0);
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

    }

    @Override
    public void onLeftItemClick(SelectedPageCategory.DataBean item) {
        //左边的分类点击了
        selectedPagePresenter.getContentByCategory(item);
        LogUtils.d(this,"current selected item -->" + item.getFavorites_title());
    }


    @Override
    public void onContentItemClick(SelectedContent.DataBean.TbkUatmFavoritesItemGetResponseBean.ResultsBean.UatmTbkItemBean item) {
        //内容被点击了
        //处理数据
        String title = item.getTitle();
        //领券页面
        String url = item.getCoupon_click_url();
        if(TextUtils.isEmpty(url)){
            //详情页地址
            url = item.getClick_url();
        }
        String cover = item.getPict_url();
        //拿到TicketPresenter去加载数据
        ITicketPresenter ticketPresenter = PresenterManager.getInstance().getTicketPresenter();
        ticketPresenter.getTicket(title, url, cover);
        startActivity(new Intent(getContext(), TicketActivity.class));
    }
}
