package com.android.learn.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.learn.R;
import com.android.learn.base.activity.BaseActivity;
import com.android.learn.base.activity.BaseMvpActivity;
import com.android.learn.base.mpresenter.BasePresenter;
import com.android.learn.base.utils.LanguageUtil;
import com.android.learn.base.view.Html5Webview;

import butterknife.BindView;
import butterknife.OnClick;

public class ArticleDetailActivity extends BaseMvpActivity {

    @BindView(R.id.webview_article)
    Html5Webview webview_article;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.iv_search)
    ImageView iv_search;
    String url;
    public static void startActivity(Context context, Bundle bundle) {
        Intent intent = new Intent(context, ArticleDetailActivity.class);

        if (bundle != null)
            intent.putExtras(bundle);
        context.startActivity(intent);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_article_detail;
    }

    @Override
    protected void initData(Bundle bundle) {
        title.setText(getString(R.string.article_detail));
        iv_back.setVisibility(View.VISIBLE);
        url = bundle.getString("url");
        webview_article.loadUrl(url);
        iv_search.setVisibility(View.VISIBLE);
        iv_search.setImageResource(R.drawable.icon_share);
    }



    @OnClick({R.id.iv_search})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.iv_search:
                share("分享地址", url);
        }
    }

    private void share(String title, String content) {
        Intent share_intent = new Intent();
        share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
        share_intent.setType("text/plain");//设置分享内容的类型
        share_intent.putExtra(Intent.EXTRA_SUBJECT, title);//添加分享内容标题
        share_intent.putExtra(Intent.EXTRA_TEXT, content);//添加分享内容
        //创建分享的Dialog
        share_intent = Intent.createChooser(share_intent, "分享");
        startActivity(share_intent);
    }

    @Override
    public BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected void loadData() {

    }
}
