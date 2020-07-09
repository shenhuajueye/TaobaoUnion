package com.example.taobaounion.ui.fragment;

import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseFragment;
import com.example.taobaounion.model.domain.Categories;
import com.example.taobaounion.presenter.impl.HomePresenterImpl;
import com.example.taobaounion.view.IHomeCallBack;

public class HomeFragment extends BaseFragment implements IHomeCallBack {

    private HomePresenterImpl homePresenter;

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initPresenter() {
        //创建Presenter
        homePresenter = new HomePresenterImpl();
        homePresenter.registerCallback(this);
    }

    @Override
    protected void loadData() {
        //加载数据
        homePresenter.getCategories();
    }

    @Override
    public void onCategoriesLoaded(Categories categories) {
        //加载的数据就会从这里回来
    }

    @Override
    protected void release() {
        //取消回调注册
        if(homePresenter!=null){
            homePresenter.unregisterCallback(this);
        }
    }
}
