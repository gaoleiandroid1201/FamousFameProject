package com.android.learn.mcontract;

import com.android.learn.base.mmodel.FeedArticleListData;
import com.android.learn.base.mmodel.FeedArticleListData.FeedArticleData;
import com.android.learn.base.mmodel.HotKeyData;

import java.util.List;

/**
 * Created by gaolei on 2018/6/18.
 */

public class MainActivityContract {

    public interface Presenter {

        void getHotKey();

    }

    public interface View {

        void showHotKey(List<HotKeyData> list);

    }
}
