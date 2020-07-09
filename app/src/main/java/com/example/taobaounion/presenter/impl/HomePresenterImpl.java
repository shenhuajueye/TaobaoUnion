package com.example.taobaounion.presenter.impl;

import com.example.taobaounion.model.Api;
import com.example.taobaounion.model.domain.Categories;
import com.example.taobaounion.presenter.IHomePresenter;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.RetrofitManager;
import com.example.taobaounion.view.IHomeCallBack;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomePresenterImpl implements IHomePresenter {
    @Override
    public void getCategories() {
        //加载分类数据
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        Api api = retrofit.create(Api.class);
        Call<Categories> task = api.getCategories();
        task.enqueue(new Callback<Categories>() {
            @Override
            public void onResponse(Call<Categories> call, Response<Categories> response) {
                //数据结果
                int code = response.code();
                LogUtils.d(HomePresenterImpl.this,"result code is-->" + code);
                if(code == HttpURLConnection.HTTP_OK){
                    //请求成功
                    Categories categories = response.body();
                    LogUtils.d(HomePresenterImpl.this,categories.toString());
                }else{
                    //请求失败
                    LogUtils.i(HomePresenterImpl.this,"请求失败。。。");
                }
            }

            @Override
            public void onFailure(Call<Categories> call, Throwable t) {
                //加载失败
                LogUtils.e(HomePresenterImpl.this,"请求错误。。。" + t);
            }
        });
    }

    @Override
    public void registerCallback(IHomeCallBack callBack) {

    }

    @Override
    public void unregisterCallback(IHomeCallBack callBack) {

    }
}
