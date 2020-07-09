package com.example.taobaounion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.taobaounion.base.BaseFragment;
import com.example.taobaounion.ui.fragment.HomeFragment;
import com.example.taobaounion.ui.fragment.RedPacketFragment;
import com.example.taobaounion.ui.fragment.SearchFragment;
import com.example.taobaounion.ui.fragment.SelectedFragment;
import com.example.taobaounion.utils.LogUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @BindView(R.id.main_navigation_bar)
    public BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment;
    private SelectedFragment selectedFragment;
    private RedPacketFragment redPacketFragment;
    private SearchFragment searchFragment1;
    private SearchFragment searchFragment;
    private FragmentManager supportFragmentManager;
    private Unbinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bind = ButterKnife.bind(this);
        initView();
        initFragment();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind != null) {
            bind.unbind();
        }
    }

    private void initFragment() {
        homeFragment = new HomeFragment();
        selectedFragment = new SelectedFragment();
        redPacketFragment = new RedPacketFragment();
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

    private void switchFragment(BaseFragment targetFragment) {
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_page_container,targetFragment);
        fragmentTransaction.commit();
    }

    private void initView() {
        HomeFragment homeFragment = new HomeFragment();
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_page_container, homeFragment);
        fragmentTransaction.commit();
    }
}