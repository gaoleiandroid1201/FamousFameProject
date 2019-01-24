package com.android.learn.adapter;

import android.content.Context;

import com.android.learn.R;
import com.android.learn.base.db.SearchRecord;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by yuepeng on 2017/6/9.
 */

public class SearchRecordAdapter extends BaseQuickAdapter<SearchRecord, BaseViewHolder> {
    Context context;
    List<SearchRecord> list;

    public SearchRecordAdapter(Context context, List<SearchRecord> list) {
        super(R.layout.item_search_record, list);
        this.context = context;
        this.list = list;
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchRecord searchRecord) {
        helper.setText(R.id.tv_history_item, searchRecord.getName());
    }
}
