package com.example.taobaounion.ui.fragment;

import android.graphics.Rect;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseFragment;
import com.example.taobaounion.model.domain.Histories;
import com.example.taobaounion.model.domain.IBaseInfo;
import com.example.taobaounion.model.domain.SearchRecommend;
import com.example.taobaounion.model.domain.SearchResult;
import com.example.taobaounion.presenter.ISearchPagePresenter;
import com.example.taobaounion.ui.adapter.LinearItemContentAdapter;
import com.example.taobaounion.ui.custom.TextFlowLayout;
import com.example.taobaounion.utils.KeyboardUtil;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.PresenterManager;
import com.example.taobaounion.utils.SizeUtils;
import com.example.taobaounion.utils.TicketUtil;
import com.example.taobaounion.utils.ToastUtils;
import com.example.taobaounion.view.ISearchCallback;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SearchFragment extends BaseFragment implements ISearchCallback, TextFlowLayout.OnFlowTextItemClickListener {

    @BindView(R.id.search_history_view)
    public TextFlowLayout mHistoriesView;

    @BindView(R.id.search_recommend_view)
    public TextFlowLayout mRecommendView;

    @BindView(R.id.search_history_container)
    public View mHistoryContainer;

    @BindView(R.id.search_recommend_container)
    public View mRecommendContainer;

    @BindView(R.id.search_history_delete)
    public View mHistoryDelete;

    @BindView(R.id.search_result_list)
    public RecyclerView mSearchList;

    @BindView(R.id.search_btn)
    public TextView mSearchBtn;

    @BindView(R.id.search_clean_btn)
    public ImageView mSearchCleanInputBtn;

    @BindView(R.id.search_input_box)
    public EditText mSearchInputBox;

    @BindView(R.id.search_result_container)
    public TwinklingRefreshLayout mRefreshContainer;

    private ISearchPagePresenter mSearchPresenter;
    private LinearItemContentAdapter searchResultAdapter;

    @Override
    protected void initPresenter() {
        mSearchPresenter = PresenterManager.getInstance().getSearchPresenter();
        mSearchPresenter.registerViewCallback(this);
        //获取搜索推荐词
        mSearchPresenter.getRecommendWords();
        //searchPresenter.doSearch("毛衣");
        mSearchPresenter.getHistories();
    }

    @Override
    protected void release() {
        if (mSearchPresenter != null) {
            mSearchPresenter.unregisterViewCallback(this);
        }
    }

    @Override
    protected void onRetryClick() {
        //重新加载内容
        if (mSearchPresenter != null) {
            mSearchPresenter.research();
        }
    }

    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_search_layout, container, false);
    }

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_search;
    }

    @Override
    protected void initListener() {
        mHistoriesView.setOnFlowTextItemClickListener(this);
        mRecommendView.setOnFlowTextItemClickListener(this);
        //监听输入框的内容变化
        mSearchInputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //变化的时候通知
                //LogUtils.d(SearchFragment.this,"input text -->" + s.toString().trim());
                //如果长度不为0,那么显示删除按钮
                //否则隐藏删除按钮
                mSearchCleanInputBtn.setVisibility(hasInput(true) ? View.VISIBLE : View.GONE);
                mSearchBtn.setText(hasInput(false) ? "搜索" : "取消");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //发起搜索
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果有内容搜索

                //如果输入框没有内容则取消
                if (hasInput(false)) {
                    //发起搜索
                    if (mSearchPresenter != null) {
                        //mSearchPresenter.doSearch(mSearchInputBox.getText().toString().trim());
                        toSearch(mSearchInputBox.getText().toString().trim());
                        KeyboardUtil.hide(getContext(), v);
                    }
                } else {
                    //隐藏键盘
                    KeyboardUtil.hide(getContext(), v);
                }
            }
        });
        //清除输入框里的内容
        mSearchCleanInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchInputBox.setText("");
                //回到历史记录界面
                switch2HistoryPage();
            }
        });

        //删除历史搜索记录
        mHistoryDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除历史
                mSearchPresenter.delHistories();
            }
        });
        mRefreshContainer.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                //加载更多内容
                if (mSearchPresenter != null) {
                    mSearchPresenter.loaderMore();
                }
            }
        });
        searchResultAdapter.setOnListItemClickListener(new LinearItemContentAdapter.OnListItemClickListener() {
            @Override
            public void onItemClick(IBaseInfo item) {
                //搜索列表内容被点击了
                TicketUtil.toTicketPage(getContext(), item);
            }
        });
        mSearchInputBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //LogUtils.d(SearchFragment.this,"actionId -->" + actionId);
                if (actionId == EditorInfo.IME_ACTION_SEARCH && mSearchPresenter != null) {
                    String keyword = v.getText().toString().trim();
                    //判断拿到的内容是否为空
                    if (TextUtils.isEmpty(keyword)) {
                        return false;
                    }
                    LogUtils.d(SearchFragment.this, "input text -->" + keyword);
                    //发起搜索
                    toSearch(keyword);
                    //mSearchPresenter.doSearch(keyword);

                }
                return false;
            }
        });
    }

    /**
     * 切换到历史和推荐界面
     */
    private void switch2HistoryPage() {
        if (mSearchPresenter != null) {
            mSearchPresenter.getHistories();
        }
        if (mRecommendView.getContentSize() != 0) {
            mRecommendContainer.setVisibility(View.VISIBLE);
        } else {
            mRecommendContainer.setVisibility(View.GONE);
        }
        //内容要隐藏
        mRefreshContainer.setVisibility(View.GONE);
    }

    private boolean hasInput(boolean containSpace) {
        if (containSpace) {
            return mSearchInputBox.getText().toString().length() > 0;
        } else {
            return mSearchInputBox.getText().toString().trim().length() > 0;
        }
    }

    @Override
    protected void initView(View rootView) {
        //设置布局管理器
        mSearchList.setLayoutManager(new LinearLayoutManager(getContext()));
        //设置适配器
        searchResultAdapter = new LinearItemContentAdapter();
        mSearchList.setAdapter(searchResultAdapter);
        mRefreshContainer.setEnableLoadmore(true);
        mRefreshContainer.setEnableRefresh(false);
        mRefreshContainer.setEnableOverScroll(true);
        mSearchList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = SizeUtils.dip2px(getContext(), 1.5f);
                outRect.bottom = SizeUtils.dip2px(getContext(), 1.5f);
            }
        });
    }

    @Override
    public void onHistoriesLoaded(Histories histories) {
        setUpState(State.SUCCESS);
        if (histories == null || histories.getHistories().size() == 0) {
            mHistoryContainer.setVisibility(View.GONE);
        } else {
            mHistoriesView.setTextList(histories.getHistories());
            //LogUtils.d(SearchFragment.this, "histories -->" + histories.getHistories());
            mHistoryContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onHistoriesDeleted() {
        //更新历史记录
        if (mSearchPresenter != null) {
            mSearchPresenter.getHistories();
        }
    }

    @Override
    public void onSearchSuccess(SearchResult result) {
        setUpState(State.SUCCESS);
        //LogUtils.d(this, "result -->" + result);
        //隐藏掉历史记录和推荐
        mHistoryContainer.setVisibility(View.GONE);
        mRecommendContainer.setVisibility(View.GONE);
        //显示搜索结果
        mRefreshContainer.setVisibility(View.VISIBLE);
        //设置数据
        try {
            searchResultAdapter.setData(result.getData()
                    .getTbk_dg_material_optional_response()
                    .getResult_list()
                    .getMap_data());
        } catch (Exception e) {
            e.printStackTrace();
            //切换到搜索内容为空
            setUpState(State.EMPTY);
        }

    }

    @Override
    public void onMoreLoaded(SearchResult result) {
        mRefreshContainer.finishLoadmore();
        //加载更多的结果
        //拿到结果，添加到适配器的尾部
        List<SearchResult.DataBean.TbkDgMaterialOptionalResponseBean.ResultListBean.MapDataBean> moreData = result.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data();
        searchResultAdapter.addData(moreData);
        //提示用户加载到更多的内容
        ToastUtils.showToast("加载到了" + moreData.size() + "条记录");
    }

    @Override
    public void onMoreLoaderError() {
        mRefreshContainer.finishLoadmore();
        ToastUtils.showToast("网络异常，请稍后重试");
    }

    @Override
    public void onMoreLoaderEmpty() {
        mRefreshContainer.finishLoadmore();
        ToastUtils.showToast("没有更多数据");
    }

    @Override
    public void onRecommendWordsLoaded(List<SearchRecommend.DataBean> recommendWords) {
        setUpState(State.SUCCESS);
        LogUtils.d(this, "recommendWords size -->" + recommendWords.size());
        List<String> recommendKeywords = new ArrayList<>();
        for (SearchRecommend.DataBean item : recommendWords) {
            setUpState(State.SUCCESS);
            recommendKeywords.add(item.getKeyword());
        }
        if (recommendWords == null || recommendWords.size() == 0) {
            mRecommendContainer.setVisibility(View.GONE);
        } else {
            mRecommendView.setTextList(recommendKeywords);
            mRecommendContainer.setVisibility(View.VISIBLE);
        }
        mRecommendView.setTextList(recommendKeywords);
    }

    @Override
    public void onError() {
        setUpState(State.ERROR);
    }

    @Override
    public void onLoading() {
        setUpState(State.LOADING);
    }

    @Override
    public void onEmpty() {
        setUpState(State.EMPTY);
    }

    @Override
    public void onFlowItemClick(String text) {
        //发起搜索
        toSearch(text);
    }

    private void toSearch(String text) {
        if (mSearchPresenter != null) {
            mSearchList.scrollToPosition(0);
            mSearchInputBox.setText(text);
            mSearchInputBox.setFocusable(true);
            mSearchInputBox.requestFocus();
            mSearchInputBox.setSelection(text.length(),text.length());
            mSearchPresenter.doSearch(text);
        }
    }
}
