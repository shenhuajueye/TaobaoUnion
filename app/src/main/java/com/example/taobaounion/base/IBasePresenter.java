package com.example.taobaounion.base;

public interface IBasePresenter<T> {
    /**
     * 注册UI通知接口
     * @param callBack
     */
    void registerViewCallback(T callBack);

    /**
     * 取消UI通知接口
     * @param callBack
     */
    void unregisterViewCallback(T callBack);
}
