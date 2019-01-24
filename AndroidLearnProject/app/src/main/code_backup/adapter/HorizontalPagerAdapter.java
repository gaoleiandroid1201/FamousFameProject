package com.android.learn.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.learn.R;
import com.android.learn.activity.KnowledgeChildActivity;
import com.android.learn.base.mmodel.TreeBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;



public class HorizontalPagerAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<TreeBean> mList = new ArrayList<>();

    public HorizontalPagerAdapter(final Context context,List<TreeBean> mList) {
        mContext = context;
        this.mList=mList;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setList(List<TreeBean> list) {
        mList = list;
    }

    @Override
    public int getCount() {
        return  mList.size();
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final View view;
        int page;
        view = mLayoutInflater.inflate(R.layout.item_tree, container, false);
        final TextView txt = (TextView) view.findViewById(R.id.txt_item);
        final TreeBean treeBean = mList.get(position);
        txt.setText(treeBean.getName());
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        ChildrenAdapter adapter = new ChildrenAdapter(treeBean.getChildren());
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                KnowledgeChildActivity.startTreeChildrenActivity(mContext,
                        treeBean.getChildren().get(position).getId(),
                        treeBean.getChildren().get(position).getName());
            }
        });
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view.equals(object);
    }

    @Override
    public int getItemPosition(final Object object) {
        return POSITION_NONE;
    }

    private class ChildrenAdapter extends BaseQuickAdapter<TreeBean.ChildrenBean, BaseViewHolder> {

        public ChildrenAdapter(@Nullable List<TreeBean.ChildrenBean> data) {
            super(R.layout.item_tree_children, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, TreeBean.ChildrenBean item) {
            helper.setText(R.id.tv, item.getName());
        }
    }
}
