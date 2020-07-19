package com.example.taobaounion.model.domain;

public interface IBaseInfo {
    /**
     * 获取商品的封面
     * @return
     */
    String getCover();

    /**
     * 获取商品的标题
     * @return
     */
    String getTitle();

    /**
     * 获取商品的Url
     * @return
     */
    String getUrl();
}
