package com.android.learn.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.learn.R;
import com.android.learn.adapter.ArticleQuickAdapter;
import com.android.learn.adapter.DividerItemDecoration;
import com.android.learn.base.activity.BaseMvpActivity;
import com.android.learn.base.mmodel.FeedArticleListData;
import com.android.learn.base.mmodel.FeedArticleListData.FeedArticleData;
import com.android.learn.base.mmodel.TodoData;
import com.android.learn.base.utils.LanguageUtil;
import com.android.learn.base.utils.LogUtil;
import com.android.learn.base.utils.Utils;
import com.android.learn.mcontract.SearchContract;
import com.android.learn.mpresenter.SearchPresenter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SearchResultActivity extends BaseMvpActivity<SearchPresenter> implements SearchContract.View {
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.tv_empty_knowledge)
    TextView tv_empty_knowledge;
    @BindView(R.id.article_recyclerview)
    RecyclerView article_recyclerview;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    private ArticleQuickAdapter feedArticleAdapter;
    String keyword;
    String TAG = "KnowledgeChildActivity";


    public static void startActivity(Context context, Bundle bundle) {
        Intent intent = new Intent(context, SearchResultActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_knowledge_article;
    }

    @Override
    protected void initData(Bundle bundle) {
        keyword = bundle.getString("key");
        title.setText(keyword);
        iv_back.setVisibility(View.VISIBLE);
        initSmartRefreshLayout();
        initRecyclerView();
    }


    @Override
    public SearchPresenter initPresenter() {
        return new SearchPresenter();
    }

    @Override
    protected void loadData() {
        mPresenter.getFeedArticleList(keyword);
    }

    @Override
    public void showArticleList(FeedArticleListData listData) {
        final List<FeedArticleData> newDataList = listData.getDatas();
        if (newDataList == null || newDataList.size() == 0) {
            smartRefreshLayout.finishLoadMoreWithNoMoreData();
        }
        smartRefreshLayout.finishLoadMore();

        feedArticleAdapter.addData(newDataList);

        if (feedArticleAdapter.getData().size() == 0) {
            tv_empty_knowledge.setVisibility(View.VISIBLE);
        } else tv_empty_knowledge.setVisibility(View.GONE);
    }

    @Override
    public void showCollectArticleData(int position, FeedArticleData feedArticleData) {
        feedArticleAdapter.setData(position, feedArticleData);
    }

    @Override
    public void showCancelCollectArticleData(int position, FeedArticleData feedArticleData) {
        feedArticleAdapter.setData(position, feedArticleData);
    }

    private void initRecyclerView() {
        List<FeedArticleData> articleDataList = new ArrayList<>();
        feedArticleAdapter = new ArticleQuickAdapter(this, articleDataList, "KnowledgeChildActivity");
        article_recyclerview.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
        article_recyclerview.setLayoutManager(new LinearLayoutManager(this));

        //解决数据加载完成后, 没有停留在顶部的问题
        article_recyclerview.setFocusable(false);
        article_recyclerview.setAdapter(feedArticleAdapter);
        feedArticleAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(SearchResultActivity.this, ArticleDetailActivity.class);
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
                mPresenter.onLoadMore(keyword);
            }


        });
    }


}
