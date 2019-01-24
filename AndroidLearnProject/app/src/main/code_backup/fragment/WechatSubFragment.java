package com.android.learn.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.learn.R;
import com.android.learn.activity.ArticleDetailActivity;
import com.android.learn.adapter.ArticleQuickAdapter;
import com.android.learn.adapter.DividerItemDecoration;
import com.android.learn.base.event.CancelCollectEvent;
import com.android.learn.base.event.LoginEvent;
import com.android.learn.base.fragment.BaseMvpFragment;
import com.android.learn.base.mmodel.FeedArticleListData;
import com.android.learn.base.mmodel.FeedArticleListData.FeedArticleData;
import com.android.learn.base.view.CustomProgressDialog;
import com.android.learn.mcontract.WechatSubContract;
import com.android.learn.mpresenter.WechatSubPresenter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class WechatSubFragment extends BaseMvpFragment<WechatSubPresenter> implements WechatSubContract.View {
    @BindView(R.id.article_recyclerview)
    RecyclerView article_recyclerview;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    private List<FeedArticleData> articleDataList;
    private ArticleQuickAdapter feedArticleAdapter;

    int userId;
    String userName;

    public static WechatSubFragment newInstance(int id, String name) {
        WechatSubFragment testFragment = new WechatSubFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putString("name", name);
        testFragment.setArguments(bundle);
        return testFragment;
    }


    @Override
    public void initData(Bundle bundle) {
        userId = bundle.getInt("id", 408);
        userName = bundle.getString("name");
        initSmartRefreshLayout();
        initRecyclerView();
    }

    @Override
    public void initView(View view) {
        EventBus.getDefault().register(this);
    }

    @Override
    public int setContentLayout() {
        return R.layout.fragment_wechat_sub;
    }

    @Override
    public void reload() {

    }

    @Override
    public WechatSubPresenter initPresenter() {
        return new WechatSubPresenter();
    }

    @Override
    protected void loadData() {
        CustomProgressDialog.show(getActivity());
        if (userId != 0)
            mPresenter.getWxArtileById(userId);
    }

    @Override
    public void showWxArticleById(FeedArticleListData datas) {
        final List<FeedArticleData> newDataList = datas.getDatas();
        if (newDataList == null || newDataList.size() == 0) {
            smartRefreshLayout.finishLoadMoreWithNoMoreData();
            return;
        }
        smartRefreshLayout.finishLoadMore();
        feedArticleAdapter.addData(newDataList);

    }

    @Override
    public void showCollectArticleData(int position, FeedArticleListData.FeedArticleData feedArticleData) {
        feedArticleAdapter.setData(position, feedArticleData);
    }

    @Override
    public void showCancelCollectArticleData(int position, FeedArticleListData.FeedArticleData feedArticleData) {
        feedArticleAdapter.setData(position, feedArticleData);
    }

    @Override
    public void showCancelCollectArticleData(int id) {
        int position = feedArticleAdapter.getPosById(id);
        if (position == -1) return;
        FeedArticleData feedArticleData = articleDataList.get(position);
        feedArticleData.setCollect(false);
        feedArticleAdapter.setData(position, feedArticleData);
    }


    private void initRecyclerView() {
        articleDataList = new ArrayList<>();
        feedArticleAdapter = new ArticleQuickAdapter(getActivity(), articleDataList, "WechatSubFragment");
        article_recyclerview.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST));
        article_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

        //解决数据加载完成后, 没有停留在顶部的问题
        article_recyclerview.setFocusable(false);
        article_recyclerview.setAdapter(feedArticleAdapter);

        feedArticleAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getActivity(), ArticleDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", feedArticleAdapter.getData().get(position).getLink());
                intent.putExtras(bundle);
                startActivity(intent);
            }

        });
        feedArticleAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (feedArticleAdapter.getData().get(position).isCollect()) {
                    mPresenter.cancelCollectArticle(position, feedArticleAdapter.getData().get(position));
                } else {
                    mPresenter.addCollectArticle(position, feedArticleAdapter.getData().get(position));
                }
            }
        });
    }

    //初始化下拉刷新控件
    private void initSmartRefreshLayout() {
        smartRefreshLayout.setEnableLoadMore(true);
        smartRefreshLayout.setEnableRefresh(false);
        smartRefreshLayout.setEnableScrollContentWhenLoaded(true);//是否在加载完成时滚动列表显示新的内容
        smartRefreshLayout.setEnableFooterFollowWhenLoadFinished(true);
        smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                mPresenter.getWxArtileById(userId);
            }


        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CancelCollectEvent event) {
        mPresenter.cancelCollectArticle(event.id);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginEvent accountEvent) {
        feedArticleAdapter.getData().clear();
        feedArticleAdapter.notifyDataSetChanged();
        mPresenter.getWxArtileById(userId);
        mPresenter.num = 0;
    }

    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
