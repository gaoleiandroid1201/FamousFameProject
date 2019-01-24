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
import com.android.learn.base.event.CancelCollectEvent;
import com.android.learn.base.mmodel.FeedArticleListData;
import com.android.learn.base.mmodel.FeedArticleListData.FeedArticleData;
import com.android.learn.base.utils.LanguageUtil;
import com.android.learn.base.view.CustomProgressDialog;
import com.android.learn.mcontract.CollectContract;
import com.android.learn.mpresenter.CollectPresenter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class MyCollectActivity extends BaseMvpActivity<CollectPresenter> implements CollectContract.View {


    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.article_collect_recyclerview)
    RecyclerView article_collect_recyclerview;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.tv_empty_collect)
    TextView tv_empty_collect;
    private List<FeedArticleData> articleDataList;
    private ArticleQuickAdapter feedArticleAdapter;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MyCollectActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_collect;
    }

    @Override
    protected void initData(Bundle bundle) {
        title.setText(getString(R.string.collect));
        iv_back.setVisibility(View.VISIBLE);
        initRecyclerView();
        initSmartRefreshLayout();

    }


    @Override
    public CollectPresenter initPresenter() {
        return new CollectPresenter();
    }

    @Override
    protected void loadData() {
        CustomProgressDialog.show(this);
        mPresenter.getCollectList();
    }


    @Override
    public void showCollectList(FeedArticleListData feedArticleListData) {
        final List<FeedArticleData> newDataList = feedArticleListData.getDatas();
        if (newDataList == null || newDataList.size() == 0) {
            smartRefreshLayout.finishLoadMoreWithNoMoreData();
        }
        smartRefreshLayout.finishLoadMore();

        feedArticleAdapter.addData(newDataList);

        if (feedArticleAdapter.getData().size() == 0) {
            tv_empty_collect.setVisibility(View.VISIBLE);
        } else tv_empty_collect.setVisibility(View.GONE);

    }

    @Override
    public void showCancelCollectArticle(int position, int id) {
        feedArticleAdapter.remove(position);
        EventBus.getDefault().post(new CancelCollectEvent(id));
    }

    private void initRecyclerView() {
        articleDataList = new ArrayList<>();
        feedArticleAdapter = new ArticleQuickAdapter(this, articleDataList, "MyCollectActivity");
        article_collect_recyclerview.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
        article_collect_recyclerview.setLayoutManager(new LinearLayoutManager(this));

        article_collect_recyclerview.setFocusable(false);
        article_collect_recyclerview.setAdapter(feedArticleAdapter);
        feedArticleAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                mPresenter.cancelCollectArticle(position, feedArticleAdapter.getData().get(position).originId);
            }
        });
        feedArticleAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(MyCollectActivity.this, ArticleDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", articleDataList.get(position).getLink());
                intent.putExtras(bundle);
                startActivity(intent);
            }

        });
    }

    private void initSmartRefreshLayout() {
        smartRefreshLayout.setEnableLoadMore(true);
        smartRefreshLayout.setEnableRefresh(false);
        smartRefreshLayout.setEnableScrollContentWhenLoaded(true);//是否在加载完成时滚动列表显示新的内容
        smartRefreshLayout.setEnableFooterFollowWhenLoadFinished(true);
        smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                mPresenter.getCollectList();

            }


        });
    }


}
