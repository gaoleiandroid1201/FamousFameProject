package com.android.learn.activity;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.learn.R;
import com.android.learn.base.activity.BaseActivity;
import com.android.learn.base.activity.BaseMvpActivity;
import com.android.learn.base.event.LogoutEvent;
import com.android.learn.base.event.RestartMainEvent;
import com.android.learn.base.mpresenter.BasePresenter;
import com.android.learn.base.utils.LanguageUtil;
import com.android.learn.base.utils.LogUtil;
import com.android.learn.base.utils.SPUtils;
import com.android.learn.base.utils.account.UserUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;


public class LanguageActivity extends BaseMvpActivity {
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.cb_system)
    AppCompatCheckBox cb_system;
    @BindView(R.id.cb_chinese)
    AppCompatCheckBox cb_chinese;
    @BindView(R.id.cb_english)
    AppCompatCheckBox cb_english;
    String TAG = "LanguageActivity";

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, LanguageActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_language;
    }

    @Override
    protected void initData(Bundle bundle) {
        title.setText(getString(R.string.language_set));
        iv_back.setVisibility(View.VISIBLE);

        int language = (Integer) SPUtils.getParam(this, "language", 0);
        if (language == 0) {
            cb_system.setChecked(true);
        } else if (language == 1) {
            cb_chinese.setChecked(true);
        } else if (language == 2) {
            cb_english.setChecked(true);
        }

    }


    private void selectLanguage(int select) {
        LanguageUtil.saveSelectLanguage(this, select);
        EventBus.getDefault().post(new RestartMainEvent(this));
    }


    @OnClick({R.id.cb_system, R.id.cb_chinese, R.id.cb_english})
    public void click(View view) {
        cb_system.setChecked(false);
        cb_chinese.setChecked(false);
        cb_english.setChecked(false);


        switch (view.getId()) {
            case R.id.cb_system:
                selectLanguage(0);
                break;
            case R.id.cb_chinese:
                selectLanguage(1);
                break;
            case R.id.cb_english:
                selectLanguage(2);
                break;

        }
    }


    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, TAG + "   onDestroy--------");
    }

    @Override
    public BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected void loadData() {

    }
}
