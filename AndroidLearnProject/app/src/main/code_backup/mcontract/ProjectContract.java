package com.android.learn.mcontract;

import com.android.learn.base.mmodel.ProjectListData;

/**
 * Created by gaolei on 2018/6/18.
 */

public class ProjectContract {

    public interface Presenter {

        void getProjectInfo(int page, int cid);

        void onRefreshMore(int cid);

        void onLoadMore(int cid);
    }

    public interface View {

        void showProjectList(ProjectListData itemBeans, boolean isRefresh);
    }
}
