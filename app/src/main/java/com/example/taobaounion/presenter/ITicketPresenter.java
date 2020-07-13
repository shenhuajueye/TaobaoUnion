package com.example.taobaounion.presenter;

import com.example.taobaounion.base.IBaseCallback;
import com.example.taobaounion.base.IBasePresenter;

public interface ITicketPresenter extends IBasePresenter<ITicketPagerCallback> {
    /**
     *获取优惠券生成淘口令
     * @param title
     * @param url
     * @param cover
     */
    void getTicket(String title,String url,String cover);
}
