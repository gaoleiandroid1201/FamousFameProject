package com.android.learn.mcontract;


import com.android.learn.base.mmodel.FeedArticleListData;
import com.android.learn.base.mmodel.FeedArticleListData.FeedArticleData;
import com.android.learn.base.mmodel.TreeBean;

import java.util.List;


public class KnowledgeChildContract {

    public interface Presenter {
        void getKnowledgeArticleList(int num,int cid);

        void onRefreshMore(int cid);

        void onLoadMore(int cid);

        void addCollectArticle(int position, FeedArticleData feedArticleData);

        void cancelCollectArticle(int position, FeedArticleData feedArticleData);
    }

    public interface View {
        void showArticleList(FeedArticleListData itemBeans, boolean isRefresh);
        void showCollectArticleData(int position, FeedArticleData feedArticleData);

        void showCancelCollectArticleData(int id, FeedArticleData feedArticleData);
    }

}
