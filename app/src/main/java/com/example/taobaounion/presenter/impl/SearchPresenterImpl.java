package com.example.taobaounion.presenter.impl;

import com.example.taobaounion.model.Api;
import com.example.taobaounion.model.domain.Histories;
import com.example.taobaounion.model.domain.SearchRecommend;
import com.example.taobaounion.model.domain.SearchResult;
import com.example.taobaounion.presenter.ISearchPagePresenter;
import com.example.taobaounion.utils.JsonCacheUtil;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.RetrofitManager;
import com.example.taobaounion.view.ISearchCallback;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchPresenterImpl implements ISearchPagePresenter {

    private final Api api;
    private ISearchCallback mSearchPageCallback = null;
    public static final int DEFAULT_PAGE = 0;
    /**
     * 搜索的当前页
     */
    private int mCurrentPage = DEFAULT_PAGE;
    private String mCurrentKeyword = null;
    private final JsonCacheUtil jsonCacheUtil;

    public SearchPresenterImpl() {
        RetrofitManager instance = RetrofitManager.getInstance();
        Retrofit retrofit = instance.getRetrofit();
        api = retrofit.create(Api.class);
        jsonCacheUtil = JsonCacheUtil.getInstance();
    }

    @Override
    public void getHistories() {
        Histories histories = jsonCacheUtil.getValueByKey(KEY_HISTORIES, Histories.class);
        if (mSearchPageCallback != null) {
            mSearchPageCallback.onHistoriesLoaded(histories);
        }
    }

    @Override
    public void delHistories() {
        jsonCacheUtil.delCache(KEY_HISTORIES);
        if (mSearchPageCallback != null) {
            mSearchPageCallback.onHistoriesDeleted();
        }
    }


    private static final String KEY_HISTORIES = "key_histories";
    public static final int DEFAULT_HISTORIES = 10;
    private int historiesMaxSize = DEFAULT_HISTORIES;

    /**
     * 添加历史记录
     *
     * @param history
     */
    private void saveHistories(String history) {
        Histories histories = jsonCacheUtil.getValueByKey(KEY_HISTORIES, Histories.class);
        //如果说已经有了，就干掉，然后再添加
        List<String> historiesList = null;
        if (histories != null && histories.getHistories() != null) {
            historiesList = histories.getHistories();
            if (historiesList.contains(history)) {
                historiesList.remove(history);
            }
        }
        //去重完成
        //处理没有数据的情况
        if (historiesList == null) {
            historiesList = new ArrayList<>();
        }
        if (histories == null) {
            histories = new Histories();
            histories.setHistories(historiesList);
        }

        //对个数进行限制
        if (historiesList.size() > historiesMaxSize) {
            historiesList = historiesList.subList(0, historiesMaxSize);
        }
        //添加记录
        historiesList.add(history);
        //保存记录
        jsonCacheUtil.saveCache(KEY_HISTORIES, histories);
    }

    @Override
    public void doSearch(String keyword) {
        if (mCurrentKeyword != keyword) {
            this.mCurrentKeyword = keyword;
        }
        //更新UI状态
        if (mSearchPageCallback == null || mCurrentKeyword.equals(keyword)) {
            this.saveHistories(keyword);
            mSearchPageCallback.onLoading();
        }
        Call<SearchResult> task = api.doSearch(mCurrentPage, keyword);
        task.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                int code = response.code();
                LogUtils.d(SearchPresenterImpl.this, "code result is -->" + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    handleSearchResult(response.body());
                } else {
                    onError();
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                t.printStackTrace();
                onError();
            }
        });
    }

    private void onError() {
        if (mSearchPageCallback != null) {
            mSearchPageCallback.onError();
        }
    }

    private void handleSearchResult(SearchResult result) {
        if (mSearchPageCallback != null) {
            if (isResultEmpty(result)) {
                //数据为空
                mSearchPageCallback.onEmpty();
            } else {
                mSearchPageCallback.onSearchSuccess(result);
            }
        }
    }

    private boolean isResultEmpty(SearchResult result) {
        try {
            return result == null || result.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data().size() == 0;
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public void research() {
        if (mCurrentKeyword == null) {
            if (mSearchPageCallback != null) {
                mSearchPageCallback.onEmpty();
            }
        } else {
            //可以重新搜索
            this.doSearch(mCurrentKeyword);
        }
    }

    @Override
    public void loaderMore() {
        mCurrentPage++;
        //进行搜索
        if (mCurrentKeyword == null) {
            //数据为空
            if (mSearchPageCallback != null) {
                mSearchPageCallback.onMoreLoaderEmpty();
            }
        }else{
            doSearchMore();
        }
    }

    private void doSearchMore() {
        Call<SearchResult> task = api.doSearch(mCurrentPage, mCurrentKeyword);
        task.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                int code = response.code();
                LogUtils.d(SearchPresenterImpl.this, "code result is -->" + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    handleMoreSearchResult(response.body());
                } else {
                    onLoaderMoreError();
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                t.printStackTrace();
                onLoaderMoreError();
            }
        });

    }

    /**
     * 处理加载更多的结果
     *
     * @param result
     */
    private void handleMoreSearchResult(SearchResult result) {
        if (mSearchPageCallback != null) {
            if (isResultEmpty(result)) {
                //数据为空
                mSearchPageCallback.onMoreLoaderEmpty();
            } else {
                mSearchPageCallback.onMoreLoaded(result);
            }
        }
    }

    /**
     * 加载更多失败
     */
    private void onLoaderMoreError() {
        mCurrentPage--;
        if (mSearchPageCallback != null) {
            mSearchPageCallback.onMoreLoaderError();
        }
    }

    @Override
    public void getRecommendWords() {
        Call<SearchRecommend> task = api.getRecommendWords();
        task.enqueue(new Callback<SearchRecommend>() {
            @Override
            public void onResponse(Call<SearchRecommend> call, Response<SearchRecommend> response) {
                int code = response.code();
                LogUtils.d(SearchPresenterImpl.this, "result code is -->" + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    //处理结果
                    if (mSearchPageCallback != null) {
                        mSearchPageCallback.onRecommendWordsLoaded(response.body().getData());
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchRecommend> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void registerViewCallback(ISearchCallback callBack) {
        this.mSearchPageCallback = callBack;
    }

    @Override
    public void unregisterViewCallback(ISearchCallback callBack) {
        this.mSearchPageCallback = null;
    }
}
