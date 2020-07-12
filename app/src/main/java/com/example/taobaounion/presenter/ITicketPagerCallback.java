package com.example.taobaounion.presenter;

import com.example.taobaounion.base.IBaseCallback;
import com.example.taobaounion.model.domain.TicketResult;

public interface ITicketPagerCallback extends IBaseCallback {
    /**
     * 淘口令加载结果
     * @param cover
     * @param result
     */
    void onTicketLoader(String cover, TicketResult result);
}
