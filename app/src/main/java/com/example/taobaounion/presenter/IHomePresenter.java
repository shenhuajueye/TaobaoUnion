package com.example.taobaounion.presenter;

import com.example.taobaounion.view.IHomeCallBack;

public interface IHomePresenter {
    /**
     * 获取商品分类
     */
    void getCategories();

    /**
     * 注册UI通知接口
     * @param callBack
     */
    void registerCallback(IHomeCallBack callBack);

    /**
     * 取消UI通知接口
     * @param callBack
     */
    void unregisterCallback(IHomeCallBack callBack);
}
