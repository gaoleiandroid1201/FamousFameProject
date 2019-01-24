package com.android.learn.mcontract;

import com.android.learn.base.mmodel.BannerListData;
import com.android.learn.base.mmodel.FeedArticleListData.FeedArticleData;
import com.android.learn.base.mmodel.FeedArticleListData;

/**
 * Created by gaolei on 2018/6/18.
 */

public class HomeContract {

    public interface Presenter {

        void getBannerInfo();

        void getFeedArticleList(int num);

        void onLoadMore();

        void onRefreshMore();

        void addCollectArticle(int position, FeedArticleData feedArticleData);

        void cancelCollectArticle(int position, FeedArticleData feedArticleData);
    }

    public interface View {

        void showArticleList(FeedArticleListData itemBeans, boolean isRefresh);

        void showBannerList(BannerListData itemBeans);

        void showCollectArticleData(int position, FeedArticleData feedArticleData);

        void showCancelCollectArticleData(int position, FeedArticleData feedArticleData);
        void showCancelCollectArticleData(int id);
    }
}
