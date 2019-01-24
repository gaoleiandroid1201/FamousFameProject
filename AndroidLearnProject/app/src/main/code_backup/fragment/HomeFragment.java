package com.android.learn.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.android.learn.R;
import com.android.learn.activity.ArticleDetailActivity;
import com.android.learn.adapter.ArticleQuickAdapter;
import com.android.learn.adapter.DividerItemDecoration;
import com.android.learn.base.event.CancelCollectEvent;
import com.android.learn.base.event.LoginEvent;
import com.android.learn.base.fragment.BaseMvpFragment;
import com.android.learn.base.mmodel.BannerListData;
import com.android.learn.base.mmodel.FeedArticleListData;
import com.android.learn.base.mmodel.FeedArticleListData.FeedArticleData;
import com.android.learn.base.view.CustomProgressDialog;
import com.android.learn.mcontract.HomeContract;
import com.android.learn.mpresenter.HomePresenter;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class HomeFragment extends BaseMvpFragment<HomePresenter> implements HomeContract.View {

    @BindView(R.id.article_recyclerview)
    RecyclerView article_recyclerview;
    @BindView(R.id.scrollview_nested)
    NestedScrollView scrollview_nested;
    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.smartRefreshLayout_home)
    SmartRefreshLayout smartRefreshLayout;

    private List<FeedArticleData> articleDataList;
    private ArticleQuickAdapter feedArticleAdapter;

    @Override
    public void initData(Bundle bundle) {

        EventBus.getDefault().register(this);
    }

    @Override
    public void initView(View view) {
        initSmartRefreshLayout();
        initRecyclerView();
    }

    @Override
    public int setContentLayout() {
        return R.layout.fragment_home;
    }

    @Override
    public void reload() {
        mPresenter.getFeedArticleList(0);
        mPresenter.getBannerInfo();
    }

    @Override
    public HomePresenter initPresenter() {
        return new HomePresenter();
    }

    @Override
    protected void loadData() {
        CustomProgressDialog.show(getActivity());
        mPresenter.getFeedArticleList(0);
        mPresenter.getBannerInfo();

    }


    @Override
    public void showArticleList(FeedArticleListData listData, boolean isRefresh) {
        final List<FeedArticleData> newDataList = listData.getDatas();
        if (newDataList == null || newDataList.size() == 0) {
            smartRefreshLayout.finishLoadMoreWithNoMoreData();
            return;
        }
        smartRefreshLayout.finishLoadMore();

        feedArticleAdapter.addData(newDataList);

    }

    @Override
    public void showBannerList(BannerListData itemBeans) {

        final List<String> linkList = new ArrayList<String>();
        List imageList = new ArrayList();
        List titleList = new ArrayList();
        int size = itemBeans.data.size();

        for (int i = 0; i < size; i++) {
            imageList.add(itemBeans.data.get(i).imagePath);
            titleList.add(itemBeans.data.get(i).title);
            linkList.add(itemBeans.data.get(i).url);
        }
        banner.setImageLoader(new com.youth.banner.loader.ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                Glide.with(getActivity()).load(path).into(imageView);
            }
        });

        //设置样式,默认为:Banner.NOT_INDICATOR(不显示指示器和标题)
        //可选样式如下:
        //1. Banner.CIRCLE_INDICATOR    显示圆形指示器
        //2. Banner.NUM_INDICATOR   显示数字指示器
        //3. Banner.NUM_INDICATOR_TITLE 显示数字指示器和标题
        //4. Banner.CIRCLE_INDICATOR_TITLE  显示圆形指示器和标题
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);//设置圆形指示器与标题
        //设置banner动画效果
//        Tansformer.CubeIn
//        Transformer.CubeOut
//        Transformer.DepthPage
//        Transformer.FlipHorizontal
//        Transformer.FlipVertical
//        banner.setBannerAnimation(Transformer.FlipHorizontal);
        banner.setIndicatorGravity(BannerConfig.CENTER);//设置指示器位置
        banner.setDelayTime(4000);//设置轮播时间
        banner.setImages(imageList);//设置图片源
        banner.setBannerTitles(titleList);//设置标题源

        banner.start();


        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                Intent intent = new Intent(getActivity(), ArticleDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", linkList.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void initRecyclerView() {
        articleDataList = new ArrayList<>();
        feedArticleAdapter = new ArticleQuickAdapter(getActivity(), articleDataList, "HomeFragment");
        article_recyclerview.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST));
        article_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        article_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()) {
            public boolean canScrollVertically() {
                //解决ScrollView里存在多个RecyclerView时滑动卡顿的问题
                //如果你的RecyclerView是水平滑动的话可以重写canScrollHorizontally方法
                return false;
            }
        });
        //解决数据加载不完的问题
//        project_recyclerview.setNestedScrollingEnabled(false);
//        project_recyclerview.setHasFixedSize(true);
//        //解决数据加载完成后, 没有停留在顶部的问题
        article_recyclerview.setFocusable(false);
        article_recyclerview.setAdapter(feedArticleAdapter);
        feedArticleAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getActivity(), ArticleDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", articleDataList.get(position).getLink());
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
        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                mPresenter.onLoadMore();
            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                mPresenter.onRefreshMore();
            }
        });
    }

    public void scrollToTop() {
        scrollview_nested.scrollTo(0, 0);
    }

    public void onResume() {
        super.onResume();

    }

    @Override
    public void showCollectArticleData(int position, FeedArticleData feedArticleData) {
        feedArticleAdapter.setData(position, feedArticleData);
    }

    @Override
    public void showCancelCollectArticleData(int position, FeedArticleData feedArticleData) {
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CancelCollectEvent event) {
        mPresenter.cancelCollectArticle(event.id);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginEvent accountEvent) {
        feedArticleAdapter.getData().clear();
        feedArticleAdapter.notifyDataSetChanged();
        mPresenter.getFeedArticleList(0);
        mPresenter.mCurrentPage = 0;
    }

    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
