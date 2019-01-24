package com.android.learn.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.android.learn.R;
import com.android.learn.base.mmodel.FeedArticleListData.FeedArticleData;
import com.android.learn.base.thirdframe.glide.ImageLoader;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class ArticleQuickAdapter extends BaseQuickAdapter<FeedArticleData, BaseViewHolder> {

    Context context;
    List<FeedArticleData> list;
    String fromWhere;

    public ArticleQuickAdapter(Context context, List<FeedArticleData> list, String fromWhere) {
        super(R.layout.item_article_list, list);
        this.context = context;
        this.list = list;
        this.fromWhere = fromWhere;
    }

    public int getPosById(int id) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (id == list.get(i).id)
                return i;
        }
        return -1;
    }

    @Override
    protected void convert(BaseViewHolder helper, FeedArticleData articleData) {
        helper.setText(R.id.tv_item_title, articleData.getTitle())
                .setText(R.id.tv_item_time, articleData.getNiceDate())
                .setText(R.id.tv_item_author, articleData.getAuthor());
        helper.addOnClickListener(R.id.iv_item_collect);
        if ("HomeFragment".equals(fromWhere)||"KnowledgeChildActivity".equals(fromWhere)||"WechatSubFragment".equals(fromWhere)) {
            if (articleData.isCollect()) {
                helper.setImageResource(R.id.iv_item_collect, R.drawable.icon_collect_select);
            } else {
                helper.setImageResource(R.id.iv_item_collect, R.drawable.icon_collect_unselect);
            }
        }
        if ("MyCollectActivity".equals(fromWhere)) {
            helper.setImageResource(R.id.iv_item_collect, R.drawable.icon_collect_select);
        }

    }
}
