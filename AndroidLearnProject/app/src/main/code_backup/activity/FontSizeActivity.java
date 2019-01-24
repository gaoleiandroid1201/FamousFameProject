package com.android.learn.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.learn.R;
import com.android.learn.base.activity.BaseActivity;
import com.android.learn.base.activity.BaseMvpActivity;
import com.android.learn.base.event.RestartMainEvent;
import com.android.learn.base.mpresenter.BasePresenter;
import com.android.learn.base.utils.LanguageUtil;
import com.android.learn.base.utils.LogUtil;
import com.android.learn.base.utils.SPUtils;
import com.android.learn.base.utils.ScreenUtils;
import com.android.learn.view.fontsliderbar.FontSliderBar;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;



public class FontSizeActivity extends BaseMvpActivity {
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.fontSliderBar)
    FontSliderBar fontSliderBar;
    @BindView(R.id.tv_chatcontent1)
    TextView tvContent1;
    @BindView(R.id.tv_chatcontent)
    TextView tvContent2;
    @BindView(R.id.iv_userhead)
    ImageView ivUserhead;
    private float textsize1, textsize2, textsize3;
    private float textSizef;//缩放比例
    private boolean isClickable = true;
    int currentIndex;
    String TAG="FontSizeActivity";

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, FontSizeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_fontsizes;
    }

    @Override
    protected void initData(Bundle bundle) {
        title.setText(getString(R.string.font_size));
        iv_back.setVisibility(View.VISIBLE);
        initData();
    }

    private void initData() {
        currentIndex = (Integer) SPUtils.getParam(this, "currentIndex", 1);
        textSizef = 1 + currentIndex * 0.1f;
        float size1 = tvContent1.getTextSize();
        float size2 = tvContent2.getTextSize();
        textsize1 = size1 / textSizef;
        textsize2 = size2 / textSizef;
        fontSliderBar.setTickCount(6).setTickHeight(ScreenUtils.dp2px(FontSizeActivity.this, 15)).setBarColor(Color.GRAY)
                .setTextColor(Color.BLACK).setTextPadding(ScreenUtils.dp2px(FontSizeActivity.this, 10)).setTextSize(ScreenUtils.dp2px(FontSizeActivity.this, 14))
                .setThumbRadius(ScreenUtils.dp2px(FontSizeActivity.this, 10)).setThumbColorNormal(Color.GRAY).setThumbColorPressed(Color.GRAY)
                .setOnSliderBarChangeListener(new FontSliderBar.OnSliderBarChangeListener() {
                    @Override
                    public void onIndexChanged(FontSliderBar rangeBar, int index) {
                        if (index > 5) {
                            return;
                        }
                        index = index - 1;
                        float textSizef = 1 + index * 0.1f;
                        setTextSize(textSizef);
                    }
                }).setThumbIndex(currentIndex).withAnimation(false).applay(this);

    }

    @OnClick({R.id.iv_back})
    public void click(View view) {

        switch (view.getId()) {
            case R.id.iv_back:
                if (fontSliderBar.getCurrentIndex() != currentIndex) {
                    if (isClickable) {
                        isClickable = false;
                        refresh();
                    }
                } else {
                    finish();
                }
                break;

        }
    }

    private void setTextSize(float textSize) {
        //改变当前页面的字体大小
        float size1 = textsize1 * textSize;
        float size2 = textsize2 * textSize;
        float size3 = textsize3 * textSize;
        tvContent1.setTextSize(ScreenUtils.px2sp(FontSizeActivity.this, size1));
        tvContent2.setTextSize(ScreenUtils.px2sp(FontSizeActivity.this, size2));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (currentIndex != fontSliderBar.getCurrentIndex()) {
                if (isClickable) {
                    isClickable = false;
                    refresh();
                }
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void refresh() {
        //存储标尺的下标
        SPUtils.setParam(this, "currentIndex", fontSliderBar.getCurrentIndex());
        //通知主页面重启
        EventBus.getDefault().post(new RestartMainEvent(this));

    }

    public void onDestroy(){
        super.onDestroy();
        LogUtil.d(TAG,TAG+"   onDestroy--------");
    }

    @Override
    public BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected void loadData() {

    }
}
