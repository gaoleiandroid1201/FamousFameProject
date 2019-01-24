package com.android.learn.mcontract;

import com.android.learn.base.mmodel.FeedArticleListData;
import com.android.learn.base.mmodel.HotKeyData;

import java.util.List;

/**
 * Created by gaolei on 2018/6/18.
 */

public class SearchContract {

    public interface Presenter {


        void getFeedArticleList(String key);

        void onLoadMore(String key);

        void addCollectArticle(int position, FeedArticleListData.FeedArticleData feedArticleData);

        void cancelCollectArticle(int position, FeedArticleListData.FeedArticleData feedArticleData);
    }

    public interface View {


        void showArticleList(FeedArticleListData itemBeans);

        void showCollectArticleData(int position, FeedArticleListData.FeedArticleData feedArticleData);

        void showCancelCollectArticleData(int id, FeedArticleListData.FeedArticleData feedArticleData);
    }
}
