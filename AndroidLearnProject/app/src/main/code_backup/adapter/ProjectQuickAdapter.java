package com.android.learn.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.android.learn.R;
import com.android.learn.base.mmodel.ProjectListData;
import com.android.learn.base.mmodel.ProjectListData.ProjectData;
import com.android.learn.base.thirdframe.glide.ImageLoader;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class ProjectQuickAdapter extends BaseQuickAdapter<ProjectData,BaseViewHolder> {
    Context context;
    public ProjectQuickAdapter(Context context, List<ProjectData> list) {
        super(R.layout.item_project_list, list);
        this.context=context;
    }

    @Override
    protected void convert(BaseViewHolder helper, ProjectData projectInfo) {
        helper.setText(R.id.item_project_list_title_tv, projectInfo.getTitle())
                .setText(R.id.item_project_list_content_tv, projectInfo.getDesc())
                .setText(R.id.item_project_list_time_tv, projectInfo.getNiceDate())
                .setText(R.id.item_project_list_author_tv, projectInfo.getAuthor());

        ImageLoader.getInstance().load(context,projectInfo.getEnvelopePic(),(ImageView) helper.getView(R.id.item_project_list_iv));
    }
}
