package com.example.taobaounion;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.taobaounion.base.BaseActivity;
import com.example.taobaounion.base.BaseFragment;
import com.example.taobaounion.ui.activity.IMainActivity;
import com.example.taobaounion.ui.fragment.HomeFragment;
import com.example.taobaounion.ui.fragment.OnSellFragment;
import com.example.taobaounion.ui.fragment.SearchFragment;
import com.example.taobaounion.ui.fragment.SelectedFragment;
import com.example.taobaounion.utils.LogUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import butterknife.BindView;
import butterknife.Unbinder;

public class MainActivity extends BaseActivity implements IMainActivity {
    @BindView(R.id.main_navigation_bar)
    public BottomNavigationView bottomNavigationView;

    private HomeFragment homeFragment;
    private SelectedFragment selectedFragment;
    private OnSellFragment redPacketFragment;
    private SearchFragment searchFragment;
    private FragmentManager supportFragmentManager;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        initFragment();
    }

    @Override
    protected void initPresenter() {

    }

    /**
     * 跳转到搜索界面
     */
    public void switch2Search() {
        //switchFragment(searchFragment);
        //切换导航栏的选中项
        bottomNavigationView.setSelectedItemId(R.id.search);

    }

    @Override
    protected void initEvent() {
        initListener();
    }


    private void initFragment() {
        homeFragment = new HomeFragment();
        selectedFragment = new SelectedFragment();
        redPacketFragment = new OnSellFragment();
        searchFragment = new SearchFragment();
        supportFragmentManager = getSupportFragmentManager();
        switchFragment(homeFragment);

    }

    private void initListener() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    LogUtils.d(this, "切换到首页");
                    switchFragment(homeFragment);
                } else if (item.getItemId() == R.id.selected) {
                    LogUtils.d(this, "切换到精选");
                    switchFragment(selectedFragment);
                } else if (item.getItemId() == R.id.red_packet) {
                    LogUtils.d(this, "切换到特惠");
                    switchFragment(redPacketFragment);
                } else if (item.getItemId() == R.id.search) {
                    LogUtils.d(this, "切换到搜索");
                    switchFragment(searchFragment);
                }
                return true;
            }
        });
    }

    /**
     * 上一次显示的Fragment
     */
    private BaseFragment lastOneFragment = null;

    private void switchFragment(BaseFragment targetFragment) {
        //如果上一个fragment跟当前要切换的fragment是同一个，那么不需要切换
        if(lastOneFragment == targetFragment){
            return;
        }

        //修改成add和hide的方式来控制Fragment的切换
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        if (!targetFragment.isAdded()) {
            fragmentTransaction.add(R.id.main_page_container, targetFragment);
        } else {
            fragmentTransaction.show(targetFragment);
        }
        if (lastOneFragment != null) {
            LogUtils.d(this, "已隐藏" + lastOneFragment);
            fragmentTransaction.hide(lastOneFragment);
        }
        lastOneFragment = targetFragment;
        fragmentTransaction.commit();
    }
}