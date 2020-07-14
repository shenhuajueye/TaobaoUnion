package com.example.taobaounion.utils;

import com.example.taobaounion.presenter.ICategoryPagerPresenter;
import com.example.taobaounion.presenter.IHomePresenter;
import com.example.taobaounion.presenter.IOnSellPagePresenter;
import com.example.taobaounion.presenter.ISelectedPagePresenter;
import com.example.taobaounion.presenter.ITicketPresenter;
import com.example.taobaounion.presenter.impl.CategoryPagerPresenterImpl;
import com.example.taobaounion.presenter.impl.HomePresenterImpl;
import com.example.taobaounion.presenter.impl.OnSellPagePresenterImpl;
import com.example.taobaounion.presenter.impl.SelectedPagePresenterImpl;
import com.example.taobaounion.presenter.impl.TicketPresenterImpl;

public class PresenterManager {
    private static final PresenterManager ourInstance = new PresenterManager();

    private final ICategoryPagerPresenter categoryPagerPresenter;
    private final IHomePresenter homePresenter;
    private final ITicketPresenter ticketPresenter;
    private final ISelectedPagePresenter selectedPagePresenter;

    public IOnSellPagePresenter getOnSellPagePresenter() {
        return onSellPagePresenter;
    }

    private final IOnSellPagePresenter onSellPagePresenter;

    public static PresenterManager getInstance() {
        return ourInstance;
    }

    public ITicketPresenter getTicketPresenter() {
        return ticketPresenter;
    }

    public static PresenterManager getOurInstance() {
        return ourInstance;
    }

    public ISelectedPagePresenter getSelectedPagePresenter() {
        return selectedPagePresenter;
    }

    private PresenterManager() {
        categoryPagerPresenter = new CategoryPagerPresenterImpl();
        homePresenter = new HomePresenterImpl();
        ticketPresenter = new TicketPresenterImpl();
        selectedPagePresenter = new SelectedPagePresenterImpl();
        onSellPagePresenter = new OnSellPagePresenterImpl();
    }

    public ICategoryPagerPresenter getCategoryPagerPresenter() {
        return categoryPagerPresenter;
    }

    public IHomePresenter getHomePresenter() {
        return homePresenter;
    }
}
