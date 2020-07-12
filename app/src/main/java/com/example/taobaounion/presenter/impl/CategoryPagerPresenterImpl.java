package com.example.taobaounion.presenter.impl;

import android.util.Log;

import com.example.taobaounion.model.Api;
import com.example.taobaounion.model.domain.HomePagerContent;
import com.example.taobaounion.presenter.ICategoryPagerPresenter;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.RetrofitManager;
import com.example.taobaounion.utils.UrlUtils;
import com.example.taobaounion.view.ICategoryPagerCallback;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CategoryPagerPresenterImpl implements ICategoryPagerPresenter {

    private Map<Integer, Integer> pageInfo = new HashMap<>();
    public static final int DEFAULT_PAGE = 1;
    private Integer currentPage;

    private CategoryPagerPresenterImpl() {

    }

    private static ICategoryPagerPresenter sInstance = null;

    public static ICategoryPagerPresenter getInstance() {
        if (sInstance == null) {
            sInstance = new CategoryPagerPresenterImpl();
        }
        return sInstance;
    }

    @Override
    public void getContentByCategoryId(int categoryId) {
        for (ICategoryPagerCallback callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                callback.onLoading();
            }
        }
        //根据分类id去加载内容
        Integer targetPage = pageInfo.get(categoryId);
        if (targetPage == null) {
            targetPage = DEFAULT_PAGE;
            pageInfo.put(categoryId, targetPage);
        }
        Call<HomePagerContent> task = createTask(categoryId, targetPage);
        task.enqueue(new Callback<HomePagerContent>() {
            @Override
            public void onResponse(Call<HomePagerContent> call, Response<HomePagerContent> response) {
                int code = response.code();
                //LogUtils.d(CategoryPagerPresenterImpl.this, "code -->" + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    HomePagerContent pagerContent = response.body();
                    //LogUtils.d(CategoryPagerPresenterImpl.this, "pagerContent -->" + pagerContent);
                    //把数据给UI更新
                    handleHomePagerContentResult(pagerContent, categoryId);
                } else {
                    handleNetworkError(categoryId);
                }
            }

            @Override
            public void onFailure(Call<HomePagerContent> call, Throwable t) {
                LogUtils.d(CategoryPagerPresenterImpl.this, "onFailure -->" + t.toString());
                handleNetworkError(categoryId);
            }
        });
    }

    private Call<HomePagerContent> createTask(int categoryId, Integer targetPage) {
        String homePagerUrl = UrlUtils.createHomePagerUrl(categoryId, targetPage);
        //LogUtils.d(this, "home pager url -->" + homePagerUrl);
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        Api api = retrofit.create(Api.class);
        return api.getHomePagerContent(homePagerUrl);
    }

    private void handleNetworkError(int categoryId) {
        for (ICategoryPagerCallback callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                callback.onError();
            }
        }
    }

    private void handleHomePagerContentResult(HomePagerContent pagerContent, int categoryId) {
        List<HomePagerContent.DataBean> data = pagerContent.getData();
        //通知UI层更新数据
        for (ICategoryPagerCallback callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                if (pagerContent == null || pagerContent.getData().size() == 0) {
                    callback.onEmpty();
                } else {
                    List<HomePagerContent.DataBean> looperData = data.subList(data.size() - 5, data.size());
                    callback.onLooperListLoader(looperData);
                    callback.onContentLoader(data);
                }
            }
        }
    }

    @Override
    public void loaderMore(int categoryId) {
        //加载更多的数据
        //1、拿到当前页码
        currentPage = pageInfo.get(categoryId);
        if (currentPage == null) {
            currentPage = 1;
        }
        //2、页码++
        currentPage++;
        //3、加载数据
        Call<HomePagerContent> task = createTask(categoryId, currentPage);
        //4、处理数据结果
        task.enqueue(new Callback<HomePagerContent>() {
            @Override
            public void onResponse(Call<HomePagerContent> call, Response<HomePagerContent> response) {
                //结果
                int code = response.code();
                LogUtils.d(CategoryPagerPresenterImpl.this, "result code -->" + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    HomePagerContent result = response.body();
                    //LogUtils.d(CategoryPagerPresenterImpl.this,"result -->" + result.toString());
                    handleLoaderResult(result, categoryId);
                } else {
                    //请求失败
                    handleLoaderMoreError(categoryId);
                }
            }

            @Override
            public void onFailure(Call<HomePagerContent> call, Throwable t) {
                //请求失败
                LogUtils.d(CategoryPagerPresenterImpl.this, t.toString());
                handleLoaderMoreError(categoryId);
            }
        });
    }

    private void handleLoaderResult(HomePagerContent result, int categoryId) {
        for (ICategoryPagerCallback callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                if (result == null || result.getData().size() == 0) {
                    callback.onLoaderMoreEmpty();
                } else {
                    callback.onLoaderMoreLoader(result.getData());
                }
            }
        }
    }

    private void handleLoaderMoreError(int categoryId) {
        currentPage--;
        pageInfo.put(categoryId,currentPage);
        for (ICategoryPagerCallback callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                callback.onLoaderMoreError();
            }
        }
    }

    @Override
    public void reload(int categoryId) {

    }

    private List<ICategoryPagerCallback> callbacks = new ArrayList<>();

    @Override
    public void registerViewCallback(ICategoryPagerCallback callBack) {
        if (!callbacks.contains(callBack)) {
            callbacks.add(callBack);
        }
    }

    @Override
    public void unregisterViewCallback(ICategoryPagerCallback callBack) {
        callbacks.remove(callBack);
    }
}
