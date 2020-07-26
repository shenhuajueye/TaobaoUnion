package com.example.taobaounion.presenter;

import com.example.taobaounion.base.IBasePresenter;
import com.example.taobaounion.view.ISearchCallback;

public interface ISearchPagePresenter extends IBasePresenter<ISearchCallback> {
    /**
     * 获取搜索历史
     */
    void getHistories();

    /**
     * 删除搜索历史
     */
    void delHistories();

    /**
     * 搜索
     * @param keyword
     */
    void doSearch(String keyword);

    /**
     * 重新搜索
     */
    void research();

    /**
     * 获取更多的搜索内容
     */
    void loaderMore();

    /**
     * 获取推荐词
     */
    void getRecommendWords();
}
