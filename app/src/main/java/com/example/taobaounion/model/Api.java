package com.example.taobaounion.model;

import com.example.taobaounion.model.domain.Categories;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Api {
    @GET("discovery/categories")
    Call<Categories> getCategories();
}
