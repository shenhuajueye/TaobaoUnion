package com.example.taobaounion.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseActivity;
import com.example.taobaounion.model.domain.TicketResult;
import com.example.taobaounion.presenter.ITicketPageCallback;
import com.example.taobaounion.presenter.ITicketPresenter;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.PresenterManager;
import com.example.taobaounion.utils.ToastUtils;
import com.example.taobaounion.utils.UrlUtils;

import butterknife.BindView;

public class TicketActivity extends BaseActivity implements ITicketPageCallback {

    private ITicketPresenter ticketPresenter;
    private boolean hasTaobaoApp = false;
    @BindView(R.id.ticket_cover)
    public ImageView mCover;

    @BindView(R.id.ticket_code)
    public EditText ticketCode;

    @BindView(R.id.ticket_copy_or_open_btn)
    public Button copyOrOpenBtn;

    @BindView(R.id.ticket_back_press)
    public ImageView ticketBackPress;

    @BindView(R.id.ticket_cover_loading)
    public View loadingView;

    @BindView(R.id.ticket_load_retry)
    public TextView retryLoading;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_ticket;
    }

    @Override
    protected void initPresenter() {
        ticketPresenter = PresenterManager.getInstance().getTicketPresenter();
        if (ticketPresenter != null) {
            ticketPresenter.registerViewCallback(this);
        }
        //判断是否安装淘宝
        //act=android.intent.action.VIEW
        // dat=http://m.taobao.com/index.htm
        // flg=0x4000000
        // cmp=com.taobao.taobao/com.taobao.tao.TBMainActivity (has extras)
        //包名是：com.taobao.taobao
        //检查是否安装淘宝
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo("com.taobao.taobao", PackageManager.MATCH_UNINSTALLED_PACKAGES);
            hasTaobaoApp = packageInfo != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            hasTaobaoApp = false;
        }
        LogUtils.d(this, "hasTaobaoApp -->" + hasTaobaoApp);
        //根据这个值来修改UI
        copyOrOpenBtn.setText(hasTaobaoApp ? "打开淘宝领券" : "复制淘口令");
    }

    @Override
    protected void release() {
        if (ticketPresenter != null) {
            ticketPresenter.unregisterViewCallback(this);
        }
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initEvent() {
        ticketBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        copyOrOpenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //复制淘口令
                //拿到内容
                String ticketCode = TicketActivity.this.ticketCode.getText().toString().trim();
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                //复制到粘贴板
                ClipData clipData = ClipData.newPlainText("sob_taobao_ticket_code", ticketCode);
                clipboardManager.setPrimaryClip(clipData);
                //判断有没有淘宝
                if (hasTaobaoApp) {
                    //如果有就打开淘宝
                    Intent taobaoIntent = new Intent();
//                    taobaoIntent.setAction("android.intent.action.MAIN");
//                    taobaoIntent.addCategory("android.intent.category.LAUNCHER");
                    ComponentName componentName = new ComponentName("com.taobao.taobao", "com.taobao.tao.TBMainActivity");
                    taobaoIntent.setComponent(componentName);
                    startActivity(taobaoIntent);
                } else {
                    //没有就提示复制成功
                    ToastUtils.showToast("已经复制,粘贴分享或打开淘宝");
                }
            }
        });
    }


    @Override
    public void onTicketLoader(String cover, TicketResult result) {
        //设置图片封面
        if (mCover != null && !TextUtils.isEmpty(cover)) {
            String coverPath = UrlUtils.getCoverPath(cover);
            Glide.with(this).load(coverPath).into(mCover);
        }

        if(TextUtils.isEmpty(cover)){
            mCover.setImageResource(R.mipmap.no_image);
        }

        //设置一下code
        if (result != null && result.getData().getTbk_tpwd_create_response() != null) {
            ticketCode.setText(result.getData().getTbk_tpwd_create_response().getData().getModel());
        }
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }


    }

    @Override
    public void onError() {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
        if (retryLoading != null) {
            retryLoading.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoading() {
        if (retryLoading != null) {
            retryLoading.setVisibility(View.GONE);
        }
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onEmpty() {

    }
}
