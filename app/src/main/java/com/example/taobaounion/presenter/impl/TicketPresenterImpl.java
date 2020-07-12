package com.example.taobaounion.presenter.impl;

import com.example.taobaounion.base.IBaseCallback;
import com.example.taobaounion.model.Api;
import com.example.taobaounion.model.domain.TicketParams;
import com.example.taobaounion.model.domain.TicketResult;
import com.example.taobaounion.presenter.ITicketPresenter;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.RetrofitManager;
import com.example.taobaounion.utils.UrlUtils;
import com.lcodecore.tkrefreshlayout.utils.LogUtil;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TicketPresenterImpl implements ITicketPresenter {
    @Override
    public void getTicket(String title, String url, String cover) {
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
                    TicketResult ticketResult = response.body();
                    LogUtils.d(TicketPresenterImpl.this,"result -->" + ticketResult);
                }else{
                    //请求失败
                }
            }

            @Override
            public void onFailure(Call<TicketResult> call, Throwable t) {

            }
        });

    }

    @Override
    public void registerViewCallback(IBaseCallback callBack) {

    }

    @Override
    public void unregisterViewCallback(IBaseCallback callBack) {

    }
}
