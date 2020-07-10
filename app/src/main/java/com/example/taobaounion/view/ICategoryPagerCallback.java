package com.example.taobaounion.view;

import com.example.taobaounion.base.IBaseCallback;
import com.example.taobaounion.model.domain.HomePagerContent;

import java.util.List;

public interface ICategoryPagerCallback extends IBaseCallback {

    /**
     * 数据加载回来
     * @param contents
     */
    void onContentLoader(List<HomePagerContent.DataBean> contents);

    int getCategoryId();

    /**
     * 加载更多网络错误
     */
    void onLoaderMoreError();

    /**
     * 加载更多没有更多内容
     */
    void onLoaderMoreEmpty();

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
