package com.example.taobaounion.view;

import com.example.taobaounion.model.domain.HomePagerContent;

import java.util.List;

public interface ICategoryPagerCallback {

    /**
     * 数据加载回来
     *
     * @param contents
     */
    void onContentLoader(List<HomePagerContent> contents);

    /**
     * 加载中
     *
     * @param categoryId
     */
    void onLoading(int categoryId);

    /**
     * 网络错误
     *
     * @param category
     */
    void onError(int category);

    /**
     * 数据为空
     *
     * @param category
     */
    void onEmpty(int category);

    /**
     * 加载更多网络错误
     * @param categoryId
     */
    void onLoaderMoreError(int categoryId);

    /**
     * 加载更多没有更多内容
     * @param categoryId
     */
    void onLoaderMoreEmpty(int categoryId);

    /**
     * 加载到更多内容
     * @param contents
     */
    void onLoaderMoreLoader(List<HomePagerContent.DataBean> contents);

    /**
     * 轮播图
     * @param contents
     */
    void onLooperListLoader(List<HomePagerContent.DataBean> contents);
}
