package com.android.learn.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.learn.R;
import com.android.learn.activity.TodoEditActivity;
import com.android.learn.adapter.DividerItemDecoration;
import com.android.learn.adapter.TodoQuickAdapter;
import com.android.learn.base.event.UpdateDoneEvent;
import com.android.learn.base.event.UpdateTodoEvent;
import com.android.learn.base.fragment.BaseMvpFragment;
import com.android.learn.base.mmodel.BaseData;
import com.android.learn.base.mmodel.TodoData;
import com.android.learn.base.mmodel.TodoData.DatasBean;
import com.android.learn.base.view.CustomProgressDialog;
import com.android.learn.mcontract.TodoContract;
import com.android.learn.mpresenter.TodoPresenter;
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


public class TodoFragment extends BaseMvpFragment<TodoPresenter> implements TodoContract.View {
    @BindView(R.id.article_recyclerview)
    RecyclerView article_recyclerview;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.tv_empty_todo)
    TextView tv_empty_todo;
    private List<TodoData.DatasBean> todoList;
    private TodoQuickAdapter todoAdapter;

    int fragmentPosition, clickId;

    public static TodoFragment newInstance(Bundle bundle) {
        TodoFragment testFragment = new TodoFragment();
        testFragment.setArguments(bundle);
        return testFragment;
    }


    @Override
    public void initData(Bundle bundle) {
        fragmentPosition = bundle.getInt("position", 0);
        initSmartRefreshLayout();
        initRecyclerView();
    }

    @Override
    public void initView(View view) {
        EventBus.getDefault().register(this);
    }

    @Override
    public int setContentLayout() {
        return R.layout.fragment_todo;
    }

    @Override
    public void reload() {

    }

    @Override
    public TodoPresenter initPresenter() {
        return new TodoPresenter();
    }

    @Override
    protected void loadData() {
        CustomProgressDialog.show(getActivity());
        if (fragmentPosition == 0)
            mPresenter.getListNotDone(0);
        if (fragmentPosition == 1)
            mPresenter.getListDone(0);
    }


    private void initRecyclerView() {
        todoList = new ArrayList<>();
        todoAdapter = new TodoQuickAdapter(getActivity(), todoList, fragmentPosition);
        article_recyclerview.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST));
        article_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

        //解决数据加载完成后, 没有停留在顶部的问题
        article_recyclerview.setFocusable(false);
        article_recyclerview.setAdapter(todoAdapter);

        todoAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DatasBean datasBean = null;
                if (fragmentPosition == 0) {
                    datasBean = todoAdapter.getData().get(position);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("todo_item", datasBean);
                    TodoEditActivity.startActivity(getActivity(), bundle);
                }
            }

        });
        todoAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                DatasBean datasBean = todoAdapter.getData().get(position);
                switch (view.getId()) {
                    case R.id.iv_item_todo:
                        if (fragmentPosition == 0) {
                            clickId = datasBean.getId();
                            mPresenter.updateTodoStatus(clickId, 1);
                            todoAdapter.remove(position);
                        }
                        if (fragmentPosition == 1) {
                            clickId = datasBean.getId();
                            mPresenter.updateTodoStatus(clickId, 0);
                            todoAdapter.remove(position);
                        }

                        break;
                    case R.id.iv_item_delete:
                        mPresenter.deleteTodo(datasBean.getId());
                        todoAdapter.remove(position);
                        break;
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
                if (fragmentPosition == 0)
                    mPresenter.getListNotDone(0);
                if (fragmentPosition == 1)
                    mPresenter.getListDone(0);
            }


        });
    }

    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void showListNotDone(TodoData todoData) {
        final List<TodoData.DatasBean> newDataList = todoData.getDatas();
        if (newDataList == null || newDataList.size() == 0) {
            smartRefreshLayout.finishLoadMoreWithNoMoreData();
        }
        smartRefreshLayout.finishLoadMore();
        todoAdapter.addData(newDataList);

        if (todoAdapter.getData().size() == 0) {
            tv_empty_todo.setVisibility(View.VISIBLE);

        } else tv_empty_todo.setVisibility(View.GONE);
    }

    @Override
    public void showListDone(TodoData todoData) {
        final List<TodoData.DatasBean> newDataList = todoData.getDatas();
        if (newDataList == null || newDataList.size() == 0) {
            smartRefreshLayout.finishLoadMoreWithNoMoreData();
        }
        smartRefreshLayout.finishLoadMore();

        todoAdapter.addData(newDataList);

        if (todoAdapter.getData().size() == 0) {
            tv_empty_todo.setVisibility(View.VISIBLE);
            tv_empty_todo.setText(getString(R.string.empty_done));
        } else tv_empty_todo.setVisibility(View.GONE);


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdateDoneEvent event) {
        if (fragmentPosition == 1) {
            todoAdapter.getData().clear();
            mPresenter.donePage = 1;
            mPresenter.getListDone(0);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdateTodoEvent event) {
        if (fragmentPosition == 0) {
            todoAdapter.getData().clear();
            mPresenter.notDonePage = 1;
            mPresenter.getListNotDone(0);
        }
    }

    @Override
    public void showUpdateTodoStatus(BaseData baseData) {
        if (fragmentPosition == 0) {
            EventBus.getDefault().post(new UpdateDoneEvent());
        }
        if (fragmentPosition == 1) {
            EventBus.getDefault().post(new UpdateTodoEvent());
        }
    }
}
