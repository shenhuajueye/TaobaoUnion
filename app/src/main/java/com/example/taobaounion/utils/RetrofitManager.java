package com.example.taobaounion.utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {
    private static final RetrofitManager outInstance = new RetrofitManager();
    private final Retrofit retrofit;

    public static RetrofitManager getInstance(){
        return outInstance;
    }

    private RetrofitManager(){
        //创建retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public Retrofit getRetrofit(){
        return retrofit;
    }
}
