package com.example.taobaounion.presenter.impl;

import com.example.taobaounion.model.Api;
import com.example.taobaounion.model.domain.SelectedContent;
import com.example.taobaounion.model.domain.SelectedPageCategory;
import com.example.taobaounion.presenter.ISelectedPagePresenter;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.RetrofitManager;
import com.example.taobaounion.utils.UrlUtils;
import com.example.taobaounion.view.ISelectedPageCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SelectedPagePresenterImpl implements ISelectedPagePresenter {

    private final Api api;

    public SelectedPagePresenterImpl(){
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        api = retrofit.create(Api.class);
    }

    private ISelectedPageCallback mViewCallback = null;

    @Override
    public void getCategories() {
        if (mViewCallback != null) {
            mViewCallback.onLoading();
        }
        //拿到retrofit
        Call<SelectedPageCategory> task = api.getSelectedPageCategories();
        task.enqueue(new Callback<SelectedPageCategory>() {
            @Override
            public void onResponse(Call<SelectedPageCategory> call, Response<SelectedPageCategory> response) {
                int resultCode = response.code();
                LogUtils.d(SelectedPagePresenterImpl.this,"getCategories result code-->" + resultCode);
                if(resultCode == HttpURLConnection.HTTP_OK){
                    SelectedPageCategory result = response.body();
                    LogUtils.d(SelectedPagePresenterImpl.this,result.toString());
                    //TODO:通知UI更新
                    if (mViewCallback != null) {
                        mViewCallback.onCategoriesLoaded(result);
                    }

                }else{
                    onLoadedError();
                }
            }

            @Override
            public void onFailure(Call<SelectedPageCategory> call, Throwable t) {
                LogUtils.d(SelectedPagePresenterImpl.this,"请求失败 -->" + t.toString());
                onLoadedError();
            }
        });
    }

    private void onLoadedError() {
        if (mViewCallback != null) {
            mViewCallback.onError();
        }
    }

    @Override
    public void getContentByCategory(SelectedPageCategory.DataBean item) {
        int categoryId = item.getFavorites_id();
        LogUtils.d(SelectedPagePresenterImpl.this,"categoryId --> " + categoryId);
        String targetUrl = UrlUtils.getSelectedPageContentUrl(categoryId);
        Call<SelectedContent> task = api.getSelectedPageContent(targetUrl);
        task.enqueue(new Callback<SelectedContent>() {
            @Override
            public void onResponse(Call<SelectedContent> call, Response<SelectedContent> response) {
                int resultCode = response.code();
                LogUtils.d(SelectedPagePresenterImpl.this,"getContentByCategory result code -->" + resultCode);
                if(resultCode == HttpURLConnection.HTTP_OK){
                    SelectedContent result = response.body();
                    if(mViewCallback!=null){
                        mViewCallback.onContentLoaded(result);
                    }
                }else{
                    onLoadedError();
                }
            }

            @Override
            public void onFailure(Call<SelectedContent> call, Throwable t) {
                onLoadedError();
            }
        });
    }

    @Override
    public void reloadContent() {
        this.getCategories();
    }

    @Override
    public void registerViewCallback(ISelectedPageCallback callBack) {
        this.mViewCallback = callBack;
    }

    @Override
    public void unregisterViewCallback(ISelectedPageCallback callBack) {
        this.mViewCallback = null;
    }
}
