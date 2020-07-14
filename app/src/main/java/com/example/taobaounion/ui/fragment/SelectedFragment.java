package com.example.taobaounion.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseFragment;
import com.example.taobaounion.model.domain.SelectedContent;
import com.example.taobaounion.model.domain.SelectedPageCategory;
import com.example.taobaounion.presenter.ISelectedPagePresenter;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.PresenterManager;
import com.example.taobaounion.view.ISelectedPageCallback;

import java.util.List;

public class SelectedFragment extends BaseFragment implements ISelectedPageCallback {

    private ISelectedPagePresenter selectedPagePresenter;

    @Override
    protected void initPresenter() {
        super.initPresenter();
        selectedPagePresenter = PresenterManager.getInstance().getSelectedPagePresenter();
        selectedPagePresenter.registerViewCallback(this);
        selectedPagePresenter.getCategories();
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
    }

    @Override
    public void onCategoriesLoaded(SelectedPageCategory categories) {
        //分类内容
        //LogUtils.d(this,"onCategoriesLoaded -->" + categories.toString());
        //TODO:更新UI
        //根据当前选中的分类，获取分类详情内容
        List<SelectedPageCategory.DataBean> data = categories.getData();
        selectedPagePresenter.getContentByCategory(data.get(0));

    }

    @Override
    public void onContentLoaded(SelectedContent content) {
        LogUtils.d(this,"onContentLoaded -->" + content.getData()
                .getTbk_uatm_favorites_item_get_response()
        .getResults()
        .getUatm_tbk_item()
        .get(0).getTitle());
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
