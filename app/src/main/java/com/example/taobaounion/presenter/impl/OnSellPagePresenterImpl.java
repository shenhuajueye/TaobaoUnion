package com.example.taobaounion.presenter.impl;

import com.example.taobaounion.model.Api;
import com.example.taobaounion.model.domain.OnSellContent;
import com.example.taobaounion.presenter.IOnSellPagePresenter;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.RetrofitManager;
import com.example.taobaounion.utils.UrlUtils;
import com.example.taobaounion.view.IOnSellPageCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OnSellPagePresenterImpl implements IOnSellPagePresenter {
    public static final int DEFAULT_PAGE = 1;
    private int mCurrentPage = DEFAULT_PAGE;
    private IOnSellPageCallback mOnSellPageCallback = null;
    private final Api api;

    public OnSellPagePresenterImpl() {
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        api = retrofit.create(Api.class);
    }

    @Override
    public void getOnSellContent() {
        if(isLoading){
            return;
        }
        isLoading = true;
        //通知UI状态为加载中
        if (mOnSellPageCallback != null) {
            mOnSellPageCallback.onLoading();
        }
        //获取特惠内容
        String targetUrl = UrlUtils.getOnSellPageUrl(mCurrentPage);
        LogUtils.d(OnSellPagePresenterImpl.this,"targetUrl is -- >" +  targetUrl);
        Call<OnSellContent> task = api.getOnSellPageContent(targetUrl);
        task.enqueue(new Callback<OnSellContent>() {
            @Override
            public void onResponse(Call<OnSellContent> call, Response<OnSellContent> response) {
                isLoading = false;
                int code = response.code();
                LogUtils.d(OnSellPagePresenterImpl.this, "result code is -->" + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    OnSellContent result = response.body();
                    onSuccess(result);
                } else {
                    onError();
                }
            }

            @Override
            public void onFailure(Call<OnSellContent> call, Throwable t) {
                onError();
            }
        });
    }

    private void onSuccess(OnSellContent result) {

        if (mOnSellPageCallback != null) {
            try {
                if (isEmpty(result)) {
                    onEmpty();
                } else {
                    mOnSellPageCallback.onContentLoadedSuccess(result);
                }
            } catch (Exception e) {
                e.printStackTrace();
                onEmpty();
            }
        }
    }

    private boolean isEmpty(OnSellContent content) {
        int size = content.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data().size();
        return size == 0;
    }

    private void onEmpty() {
        if (mOnSellPageCallback != null) {
            mOnSellPageCallback.onEmpty();
        }
    }

    private void onError() {
        isLoading = false;
        if (mOnSellPageCallback != null) {
            mOnSellPageCallback.onError();
        }
    }

    @Override
    public void reload() {
        //重新加载
        this.getOnSellContent();
    }

    /**
     * 当前状态
     */
    private boolean isLoading = false;
    @Override
    public void loaderMore() {
        if(isLoading){
            return;
        }
        isLoading = true;
        //加载更多
        mCurrentPage++;
        //去加载更多内容
        String targetUrl = UrlUtils.getOnSellPageUrl(mCurrentPage);

        Call<OnSellContent> task = api.getOnSellPageContent(targetUrl);
        task.enqueue(new Callback<OnSellContent>() {
            @Override
            public void onResponse(Call<OnSellContent> call, Response<OnSellContent> response) {
                isLoading = false;
                int code = response.code();
//                LogUtils.d(OnSellPagePresenterImpl.this, "result code is -->" + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    OnSellContent result = response.body();
                    onMoreLoaded(result);
                } else {
                    onMoreLoaderError();
                }
            }

            @Override
            public void onFailure(Call<OnSellContent> call, Throwable t) {
                onMoreLoaderError();
            }
        });
    }

    private void onMoreLoaderError() {
        isLoading = false;
        mCurrentPage--;
        mOnSellPageCallback.onMoreLoadedError();
    }

    /**
     * 加载更多的结果，通知UI更新
     * @param result
     */
    private void onMoreLoaded(OnSellContent result) {

        if (mOnSellPageCallback != null) {
            if (!isEmpty(result)) {
                mOnSellPageCallback.onMoreLoaded(result);
            }else{
                mCurrentPage--;
                mOnSellPageCallback.onMoreLoadedEmpty();
            }
        }
    }

    @Override
    public void registerViewCallback(IOnSellPageCallback callBack) {
        this.mOnSellPageCallback = callBack;
    }

    @Override
    public void unregisterViewCallback(IOnSellPageCallback callBack) {
        mOnSellPageCallback = null;
    }
}
