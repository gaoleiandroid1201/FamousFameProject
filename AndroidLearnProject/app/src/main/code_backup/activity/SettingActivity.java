package com.android.learn.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.learn.R;
import com.android.learn.base.activity.BaseActivity;
import com.android.learn.base.activity.BaseMvpActivity;
import com.android.learn.base.event.ChangeNightEvent;
import com.android.learn.base.event.LogoutEvent;
import com.android.learn.base.event.RestartMainEvent;
import com.android.learn.base.mpresenter.BasePresenter;
import com.android.learn.base.thirdframe.retrofit.RetrofitProvider;
import com.android.learn.base.utils.LanguageUtil;
import com.android.learn.base.utils.SPUtils;
import com.android.learn.base.utils.Utils;
import com.android.learn.base.utils.account.UserUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;


public class SettingActivity extends BaseMvpActivity {
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.tv_versionName)
    TextView tv_versionName;
    @BindView(R.id.help_feedback_layout)
    LinearLayout help_feedback_layout;
    @BindView(R.id.version_update_layout)
    LinearLayout version_update_layout;
    @BindView(R.id.language_switch_layout)
    LinearLayout language_switch_layout;
    @BindView(R.id.font_size_layout)
    LinearLayout font_size_layout;
    @BindView(R.id.my_logout_layout)
    LinearLayout my_logout_layout;
    @BindView(R.id.cb_setting_night)
    AppCompatCheckBox cb_setting_night;


    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }



    @Override
    protected void initData(Bundle bundle) {
        title.setText(getString(R.string.my_setting));
        iv_back.setVisibility(View.VISIBLE);
        tv_versionName.setText(Utils.getVersionName(this));
        Boolean isNightMode = (Boolean) SPUtils.getParam(this, "nightMode", new Boolean(false));
        if (isNightMode) {
            cb_setting_night.setChecked(true);
        } else {
            cb_setting_night.setChecked(false);
        }
        cb_setting_night.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                    SPUtils.setParam(SettingActivity.this, "nightMode", new Boolean(true));
                else SPUtils.setParam(SettingActivity.this, "nightMode", new Boolean(false));
                useNightMode(isChecked);
                EventBus.getDefault().post(new ChangeNightEvent());
                setStatusBar();

            }
        });
        EventBus.getDefault().register(this);
    }


    @OnClick({R.id.help_feedback_layout, R.id.version_update_layout, R.id.language_switch_layout, R.id.font_size_layout, R.id.my_logout_layout})
    public void click(View view) {

        switch (view.getId()) {
            case R.id.help_feedback_layout:
                FeedbackActivity.startActivity(SettingActivity.this);
                break;
            case R.id.language_switch_layout:
                LanguageActivity.startActivity(SettingActivity.this);
                break;
            case R.id.font_size_layout:
                FontSizeActivity.startActivity(SettingActivity.this);
                break;
            case R.id.my_logout_layout:
                if (!UserUtil.isLogined()) {
                    RegisterLoginActivity.startActivity(this);
                    return;
                }
                UserUtil.setLogined(false);
                EventBus.getDefault().post(new LogoutEvent());
                RetrofitProvider.getInstance().sharedPrefsCookiePersistor.clear();
                finish();
                break;
        }
    }

    @Override
    public BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected void loadData() {

    }

    //    private void checkUpdate() {
//
//        if (OnlineParamUtil.getParamResData() != null && OnlineParamUtil.getParamResData().rspBody != null) {
//            String android_versionCode = OnlineParamUtil.getParamResData().rspBody.android_versionCode.content.trim();
//
//            String android_update_content = OnlineParamUtil.getParamResData().rspBody.android_update_content.content.trim();
//            String android_must_update = OnlineParamUtil.getParamResData().rspBody.android_must_update.content.trim();
//            final String android_app_download_url = OnlineParamUtil.getParamResData().rspBody.android_app_download_url.content.trim();
//            if (Utils.stringToInt(android_versionCode) <= Utils.getVersionCode(this)) {
//                Utils.showToast("当前是最新版本", true);
//                return;
//            }
//            AlertDialog.Builder builder;
//            builder = new AlertDialog.Builder(SettingActivity.this);
//            builder.setTitle("升级提醒");
//            builder.setIcon(R.drawable.update);
//            builder.setMessage(android_update_content);
//            builder.setCancelable(false);
//            if ("false".equals(android_must_update)) {
//                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//
//                    }
//                });
//            }
//            builder.setPositiveButton("升级", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                    UpdateApk.downFile(android_app_download_url, SettingActivity.this);
//                }
//            });
//            builder.show();
//        }
//    }
    int yourChoice;

    private void showSingleChoiceDialog() {
        final String[] items = {"跟随系统", "简体中文", "English"};
        yourChoice = -1;
        AlertDialog.Builder singleChoiceDialog =
                new AlertDialog.Builder(SettingActivity.this);
        singleChoiceDialog.setTitle("选择语言");
        // 第二个参数是默认选项，此处设置为0
        singleChoiceDialog.setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        yourChoice = which;
                    }
                });
        singleChoiceDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SPUtils.setParam(SettingActivity.this, "languageIndex", which);
                    }
                });
        singleChoiceDialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RestartMainEvent event) {
        finish();
    }
    public void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
