package com.android.learn.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.learn.R;
import com.android.learn.activity.ArticleDetailActivity;
import com.android.learn.adapter.DividerItemDecoration;
import com.android.learn.adapter.ProjectQuickAdapter;
import com.android.learn.base.fragment.BaseMvpFragment;
import com.android.learn.base.mmodel.ProjectListData;
import com.android.learn.base.view.CustomProgressDialog;
import com.android.learn.mcontract.ProjectContract;
import com.android.learn.mpresenter.ProjectPresenter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class ProjectFragment extends BaseMvpFragment<ProjectPresenter> implements ProjectContract.View {

    @BindView(R.id.project_recyclerview)
    RecyclerView project_recyclerview;
    @BindView(R.id.smartRefreshLayout_home)
    SmartRefreshLayout smartRefreshLayout;
    ProjectQuickAdapter projectAdapter;
    private List<ProjectListData.ProjectData> projectDataList;

    @Override
    public void initData(Bundle bundle) {

    }

    @Override
    public void initView(View view) {
        initSmartRefreshLayout();
        initRecyclerView();
    }

    @Override
    public int setContentLayout() {
        return R.layout.fragment_project;
    }

    @Override
    public void reload() {
        mPresenter.getProjectInfo(1, 294);
    }

    @Override
    public ProjectPresenter initPresenter() {
        return new ProjectPresenter();
    }

    @Override
    protected void loadData() {
        CustomProgressDialog.show(getActivity());
        mPresenter.getProjectInfo(1, 294);
    }


    @Override
    public void showProjectList(ProjectListData listData, boolean isRefresh) {
        final List<ProjectListData.ProjectData> newDataList = listData.data.getDatas();
        if (newDataList == null || newDataList.size() == 0) {
            smartRefreshLayout.finishLoadMoreWithNoMoreData();
            return;
        }
        smartRefreshLayout.finishLoadMore();
        projectAdapter.addData(newDataList);

    }

    private void initRecyclerView() {
        projectDataList = new ArrayList<>();
        projectAdapter = new ProjectQuickAdapter(getActivity(), projectDataList);
        project_recyclerview.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST));
        project_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        project_recyclerview.setAdapter(projectAdapter);

        projectAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getActivity(), ArticleDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", projectDataList.get(position).getLink());
                intent.putExtras(bundle);
                startActivity(intent);
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
                mPresenter.onLoadMore(294);
            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                mPresenter.onRefreshMore(294);
            }
        });
    }

    public void scrollToTop() {
        project_recyclerview.scrollToPosition(0);
    }
}
