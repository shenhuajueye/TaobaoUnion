package com.example.taobaounion.ui.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseFragment;
import com.example.taobaounion.model.domain.Categories;
import com.example.taobaounion.model.domain.HomePagerContent;
import com.example.taobaounion.model.domain.IBaseInfo;
import com.example.taobaounion.presenter.ICategoryPagerPresenter;
import com.example.taobaounion.presenter.ITicketPresenter;
import com.example.taobaounion.ui.activity.TicketActivity;
import com.example.taobaounion.ui.adapter.HomePagerContentAdapter;
import com.example.taobaounion.ui.adapter.LooperPagerAdapter;
import com.example.taobaounion.ui.custom.AutoLooperViewPager;
import com.example.taobaounion.utils.Constants;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.PresenterManager;
import com.example.taobaounion.utils.SizeUtils;
import com.example.taobaounion.utils.TicketUtil;
import com.example.taobaounion.utils.ToastUtils;
import com.example.taobaounion.view.ICategoryPagerCallback;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.views.TbNestedScrollView;

import java.util.List;

import butterknife.BindView;

public class HomePagerFragment extends BaseFragment implements ICategoryPagerCallback, HomePagerContentAdapter.OnListItemClickListener, LooperPagerAdapter.OnLooperPagerItemClickListener {

    private ICategoryPagerPresenter categoryPagerPresenter;
    private int materialId;
    private HomePagerContentAdapter contentAdapter;
    private LooperPagerAdapter looperPagerAdapter;

    public static HomePagerFragment newInstance(Categories.DataBean category) {
        HomePagerFragment homePagerFragment = new HomePagerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_HOME_PAGER_TITLE, category.getTitle());
        bundle.putInt(Constants.KEY_HOME_PAGER_MATERIAL_ID, category.getId());
        homePagerFragment.setArguments(bundle);
        return homePagerFragment;
    }

    @BindView(R.id.home_pager_content_list)
    public RecyclerView contentList;

    @BindView(R.id.looper_pager)
    public AutoLooperViewPager looperPager;

    @BindView(R.id.home_pager_title)
    public TextView currentCategoryTitleTv;


    @BindView(R.id.looper_point_container)
    public LinearLayout looperPointContainer;

    @BindView(R.id.home_pager_parent)
    public LinearLayout homePagerParent;

    @BindView(R.id.hme_pager_header_container)
    public LinearLayout homePagerHeaderContainer;

    @BindView(R.id.home_pager_nested_scroll)
    public TbNestedScrollView homePagerNestedScroll;


    @BindView(R.id.home_pager_refresh)
    public TwinklingRefreshLayout twinklingRefreshLayout;


    @Override
    public void onResume() {
        super.onResume();
        //可见的时候去调用开始轮播
        looperPager.startLooper();
    }

    @Override
    public void onPause() {
        super.onPause();
        //不可见的时候暂停轮播
        looperPager.stopLooper();
    }

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_home_pager;
    }

    @Override
    protected void initListener() {
        contentAdapter.setOnListItemClickListener(this);
        looperPagerAdapter.setOnLooperPagerItemClickListener(this);
        homePagerParent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (homePagerHeaderContainer == null) {
                    return;
                }
                int headerHeight = homePagerHeaderContainer.getMeasuredHeight();
                //LogUtils.d(HomePagerFragment.this, "headerHeight -->" + headerHeight);
                homePagerNestedScroll.setHeaderHeight(headerHeight);
                int measuredHeight = homePagerParent.getMeasuredHeight();
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) contentList.getLayoutParams();
                layoutParams.height = measuredHeight;
                contentList.setLayoutParams(layoutParams);
                if (measuredHeight != 0) {
                    homePagerParent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                //LogUtils.d(HomePagerFragment.this,"homePagerParent -->" + measuredHeight);
            }
        });
        looperPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (looperPagerAdapter.getDataSize() == 0) {
                    return;
                }
                int targetPosition = position % looperPagerAdapter.getDataSize();
                //切换指示器
                updateLooperIndicator(targetPosition);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        twinklingRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                //LogUtils.d(HomePagerFragment.this, "触发了loader more...");
                //TODO:去加载更多内容
                if (categoryPagerPresenter != null) {
                    categoryPagerPresenter.loaderMore(materialId);
                }
            }
        });
    }

    /**
     * 切换指示器
     *
     * @param targetPosition
     */
    private void updateLooperIndicator(int targetPosition) {
        for (int i = 0; i < looperPointContainer.getChildCount(); i++) {
            View point = looperPointContainer.getChildAt(i);
            //LogUtils.d(this, "targetPosition -->" + targetPosition);
            if (i == targetPosition) {
                point.setBackgroundResource(R.drawable.shape_indicator_point_selected);
            } else {
                point.setBackgroundResource(R.drawable.shape_indicator_point_normal);
            }
        }
    }

    @Override
    protected void initView(View rootView) {
        //设置布局管理器
        contentList.setLayoutManager(new LinearLayoutManager(getContext()));
        contentList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = 8;
                outRect.bottom = 8;
            }
        });
        //创建适配器
        contentAdapter = new HomePagerContentAdapter();
        //设置适配器
        contentList.setAdapter(contentAdapter);
        //创建轮播图适配器
        looperPagerAdapter = new LooperPagerAdapter();
        //设置适配器
        looperPager.setAdapter(looperPagerAdapter);

        //设置refresh相干属性
        twinklingRefreshLayout.setEnableRefresh(false);
        twinklingRefreshLayout.setEnableLoadmore(true);

    }

    @Override
    protected void initPresenter() {
        categoryPagerPresenter = PresenterManager.getInstance().getCategoryPagerPresenter();
        categoryPagerPresenter.registerViewCallback(this);
    }

    @Override
    protected void loadData() {
        Bundle arguments = getArguments();
        String title = arguments.getString(Constants.KEY_HOME_PAGER_TITLE);
        materialId = arguments.getInt(Constants.KEY_HOME_PAGER_MATERIAL_ID);
        //TODO:加载数据
        //LogUtils.d(this, "title-->" + title);
        //LogUtils.d(this, "materialId-->" + materialId);
        if (categoryPagerPresenter != null) {
            categoryPagerPresenter.getContentByCategoryId(materialId);
        }

        if (currentCategoryTitleTv != null) {
            currentCategoryTitleTv.setText(title);
        }
    }

    @Override
    public void onContentLoader(List<HomePagerContent.DataBean> contents) {
        //数据列表加载到了
        //TODO:更新UI
        contentAdapter.setData(contents);
        setUpState(State.SUCCESS);
    }

    @Override
    public int getCategoryId() {
        return materialId;
    }

    @Override
    public void onLoading() {
        setUpState(State.LOADING);
    }

    @Override
    public void onError() {
        //网络错误
        setUpState(State.ERROR);
    }

    @Override
    public void onEmpty() {
        setUpState(State.EMPTY);
    }

    @Override
    public void onLoaderMoreError() {
        ToastUtils.showToast("网络异常，请稍后重试。。。");
        if (twinklingRefreshLayout != null) {
            twinklingRefreshLayout.finishLoadmore();
        }
    }

    @Override
    public void onLoaderMoreEmpty() {
        ToastUtils.showToast("没有更多商品了");
        if (twinklingRefreshLayout != null) {
            twinklingRefreshLayout.finishLoadmore();
        }
    }

    @Override
    public void onLoaderMoreLoader(List<HomePagerContent.DataBean> contents) {
        //添加到适配器数据的底部
        contentAdapter.addData(contents);
        if (twinklingRefreshLayout != null) {
            twinklingRefreshLayout.finishLoadmore();
        }
        ToastUtils.showToast("加载了" + contents.size() + "件商品");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onLooperListLoader(List<HomePagerContent.DataBean> contents) {
        //LogUtils.d(this, "looper size -->" + contents.size());
        looperPagerAdapter.setData(contents);
        //中间点%数据的size不一定为0，所以显示的就不是第一个
        //处理一下
        int dx = (Integer.MAX_VALUE / 2) % contents.size();
        int targetCenterPosition = (Integer.MAX_VALUE / 2) - dx;
        //设置到中间点
        looperPager.setCurrentItem(targetCenterPosition);
        looperPointContainer.removeAllViews();

        //添加点
        for (int i = 0; i < contents.size(); i++) {
            View point = new View(getContext());
            int size = SizeUtils.dip2px(getContext(), 8);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, SizeUtils.dip2px(getContext(), 8));
            layoutParams.leftMargin = SizeUtils.dip2px(getContext(), 5);
            layoutParams.rightMargin = SizeUtils.dip2px(getContext(), 5);
            point.setLayoutParams(layoutParams);
            if (i == 0) {
                point.setBackgroundResource(R.drawable.shape_indicator_point_selected);
            } else {
                point.setBackgroundResource(R.drawable.shape_indicator_point_normal);
            }
            looperPointContainer.addView(point);
        }
    }

    @Override
    protected void release() {
        if (categoryPagerPresenter != null) {
            categoryPagerPresenter.unregisterViewCallback(this);
        }
    }

    @Override
    public void onItemClick(IBaseInfo item) {
        //列表内容被点击了
        LogUtils.d(this, "recycleView item click -->" + item.getTitle());
        handleItemClick(item);
    }

    private void handleItemClick(IBaseInfo item) {
        TicketUtil.toTicketPage(getContext(),item);
    }

    @Override
    public void onLooperPagerItemClickListener(IBaseInfo item) {
        //轮播图内容被点击了
        LogUtils.d(this, "looper item click -->" + item.getTitle());
        handleItemClick(item);
    }
}
