package com.android.learn.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;

import com.android.learn.MainActivity;
import com.android.learn.R;
import com.android.learn.base.activity.BaseMvpActivity;
import com.android.learn.base.mmodel.RegisterLoginData;
import com.android.learn.base.utils.SPUtils;
import com.android.learn.base.utils.account.UserUtil;
import com.android.learn.mcontract.SplashLoginContract;
import com.android.learn.mpresenter.SplashLoginPresenter;
import com.jaeger.library.StatusBarUtil;

public class SplashActivity extends BaseMvpActivity<SplashLoginPresenter> implements SplashLoginContract.View {


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initData(Bundle bundle) {
    }

    @Override
    public SplashLoginPresenter initPresenter() {
        return new SplashLoginPresenter();
    }

    @Override
    protected void loadData() {
        String phone_num = (String) SPUtils.getParam(this, "username", "");
        String password = (String) SPUtils.getParam(this, "password", "");
        if (phone_num.length() > 0 && password.length() > 0) {
            getMPresenter().login(phone_num, password);
        } else handler.sendEmptyMessageDelayed(0, 2000);
    }

    public void jumpToMainActivity(View view) {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        handler.removeCallbacksAndMessages(null);
        finish();
    }


    @Override
    public void showLoginResData(RegisterLoginData loginResData) {
        UserUtil.setUserInfo(loginResData);
        handler.sendEmptyMessageDelayed(0, 1500);
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setTransparent(this);
    }

    public void onDestroy() {
        super.onDestroy();
    }
}
