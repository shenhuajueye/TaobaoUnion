package com.example.taobaounion.ui.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseFragment;
import com.example.taobaounion.model.domain.Categories;
import com.example.taobaounion.presenter.IHomePresenter;
import com.example.taobaounion.ui.activity.IMainActivity;
import com.example.taobaounion.ui.activity.ScanQrCodeActivity;
import com.example.taobaounion.ui.adapter.HomePagerAdapter;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.PresenterManager;
import com.example.taobaounion.view.IHomeCallBack;
import com.google.android.material.tabs.TabLayout;
import com.vondear.rxfeature.activity.ActivityScanerCode;

import butterknife.BindView;

public class HomeFragment extends BaseFragment implements IHomeCallBack {
    @BindView(R.id.home_indicator)
    public TabLayout tabLayout;

    @BindView(R.id.home_pager)
    public ViewPager homePager;

    @BindView(R.id.home_search_input_box)
    public View mSearchInputBox;

    @BindView(R.id.scan_icon)
    public View scanBtn;

    private IHomePresenter homePresenter;
    private HomePagerAdapter homePagerAdapter;

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(View rootView) {
        setUpState(State.SUCCESS);
        tabLayout.setupWithViewPager(homePager);
        //给ViewPager设计适配器
        homePagerAdapter = new HomePagerAdapter(getChildFragmentManager());
        homePager.setAdapter(homePagerAdapter);
    }

    @Override
    protected void initListener() {
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到扫描界面
                startActivity(new Intent(getContext(), ScanQrCodeActivity.class));
            }
        });
    }

    @Override
    protected void initPresenter() {
        //创建Presenter
        homePresenter = PresenterManager.getInstance().getHomePresenter();
        homePresenter.registerViewCallback(this);
        mSearchInputBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到搜索界面
                FragmentActivity activity = getActivity();
                if (activity instanceof IMainActivity) {
                    ((IMainActivity) activity).switch2Search();
                }
            }
        });
    }

    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.base_home_fragment_layout, container, false);
    }

    @Override
    protected void loadData() {
        setUpState(State.LOADING);
        //加载数据
        homePresenter.getCategories();
    }


    @Override
    public void onCategoriesLoaded(Categories categories) {
        setUpState(State.SUCCESS);
        LogUtils.d(this, "onCategoriesLoaded...");
        //加载的数据就会从这里回来
        if (homePagerAdapter != null) {
            //homePager.setOffscreenPageLimit(categories.getData().size());
            homePagerAdapter.setCategories(categories);
        }
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
    protected void release() {
        //取消回调注册
        if (homePresenter != null) {
            homePresenter.unregisterViewCallback(this);
        }
    }

    @Override
    protected void onRetryClick() {
        //网络错误，点击了重试
        //重新加载分类
        if (homePresenter != null) {
            homePresenter.getCategories();
        }
    }
}
