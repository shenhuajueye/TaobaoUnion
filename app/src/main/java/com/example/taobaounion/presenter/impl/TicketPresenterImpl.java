package com.example.taobaounion.presenter.impl;

import com.example.taobaounion.model.Api;
import com.example.taobaounion.model.domain.TicketParams;
import com.example.taobaounion.model.domain.TicketResult;
import com.example.taobaounion.presenter.ITicketPageCallback;
import com.example.taobaounion.presenter.ITicketPresenter;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.RetrofitManager;
import com.example.taobaounion.utils.UrlUtils;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TicketPresenterImpl implements ITicketPresenter {
    private ITicketPageCallback mViewCallback = null;
    private String mCover = null;
    private TicketResult ticketResult;

    enum LoadState{
        LOADING,SUCCESS,ERROR,NONE
    }

    private LoadState currentState = LoadState.NONE;

    @Override
    public void getTicket(String title, String url, String cover) {
        this.onTicketLoading();
        mCover = cover;
        //去获取淘口令
        LogUtils.d(TicketPresenterImpl.this,"title -->" + title);
        LogUtils.d(TicketPresenterImpl.this,"url -->" + url);
        LogUtils.d(TicketPresenterImpl.this,"cover -->" + cover);
        String targetUrl = UrlUtils.getTicketUrl(url);
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        Api api = retrofit.create(Api.class);
        TicketParams ticketParams = new TicketParams(targetUrl,title);
        Call<TicketResult> task = api.getTicket(ticketParams);
        task.enqueue(new Callback<TicketResult>() {
            @Override
            public void onResponse(Call<TicketResult> call, Response<TicketResult> response) {
                int code = response.code();
                LogUtils.d(TicketPresenterImpl.this,"code result -->" + code);
                if(code == HttpURLConnection.HTTP_OK){
                    //请求成功
                    ticketResult = response.body();
                    LogUtils.d(TicketPresenterImpl.this,"result -->" + ticketResult);
                    //通知UI更新
                    onTicketLoaderSuccess();
                }else{
                    //
                    onLoaderTicketError();

                }
            }

            @Override
            public void onFailure(Call<TicketResult> call, Throwable t) {
                //失败
                onLoaderTicketError();

            }
        });

    }

    private void onTicketLoaderSuccess() {
        if (mViewCallback != null) {
            mViewCallback.onTicketLoader(mCover, ticketResult);
        }else{
            currentState = LoadState.SUCCESS;
        }
    }

    private void onLoaderTicketError() {
        if (mViewCallback != null) {
            mViewCallback.onError();
        }else{
            currentState = LoadState.ERROR;
        }
    }

    @Override
    public void registerViewCallback(ITicketPageCallback callBack) {
        this.mViewCallback = callBack;
        if(currentState!=LoadState.NONE){
            //说明状态已经改变
            //更新UI
            if(currentState==LoadState.SUCCESS){
                onTicketLoaderSuccess();
            }else if(currentState ==LoadState.ERROR){
                onLoaderTicketError();
            }else if(currentState==LoadState.LOADING){
                onTicketLoading();
            }
        }
    }

    private void onTicketLoading() {
        if(mViewCallback!=null){
            mViewCallback.onLoading();
        }else{
            currentState = LoadState.LOADING;
        }
    }

    @Override
    public void unregisterViewCallback(ITicketPageCallback callBack) {
        mViewCallback = null;
    }
}
