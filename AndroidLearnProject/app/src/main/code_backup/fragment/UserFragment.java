package com.android.learn.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.learn.R;
import com.android.learn.activity.MyCollectActivity;
import com.android.learn.activity.MyTodoActivity;
import com.android.learn.activity.RegisterLoginActivity;
import com.android.learn.activity.SettingActivity;
import com.android.learn.base.event.LoginEvent;
import com.android.learn.base.event.LogoutEvent;
import com.android.learn.base.fragment.BaseMvpFragment;
import com.android.learn.base.utils.SPUtils;
import com.android.learn.base.utils.Utils;
import com.android.learn.base.utils.account.UserUtil;
import com.android.learn.mcontract.UserInfoContract;
import com.android.learn.mpresenter.UserInfoPresenter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;


public class UserFragment extends BaseMvpFragment<UserInfoPresenter> implements UserInfoContract.View {

    @BindView(R.id.iv_user_photo)
    public ImageView iv_user_photo;
    @BindView(R.id.tv_user_profile_not_login)
    TextView tv_user_profile_not_login;

    @Override
    public void initData(Bundle bundle) {
        EventBus.getDefault().register(this);
    }

    @Override
    public void initView(View view) {
        refreshUserInfo();
    }

    @OnClick({R.id.iv_user_photo, R.id.my_setting_layout, R.id.my_collect_layout, R.id.my_todo_layout})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.my_collect_layout:
                if (!UserUtil.isLogined()) {
                    RegisterLoginActivity.startActivity(getActivity());
                    Utils.showToast(getString(R.string.user_not_login), true);
                    return;
                }
                MyCollectActivity.startActivity(getActivity());
                break;
            case R.id.my_todo_layout:
                MyTodoActivity.startActivity(getActivity());
                break;
            case R.id.my_setting_layout:
                SettingActivity.startActivity(getActivity());
                break;

            case R.id.iv_user_photo:
                if (!UserUtil.isLogined()) {
                    RegisterLoginActivity.startActivity(getActivity());
                }
        }
    }

    @Override
    public int setContentLayout() {
        return R.layout.fragment_user;
    }

    @Override
    public void reload() {
    }

    @Override
    public UserInfoPresenter initPresenter() {
        return new UserInfoPresenter();
    }

    @Override
    protected void loadData() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginEvent accountEvent) {
        refreshUserInfo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LogoutEvent logoutEvent) {
        logout();
    }


    public void refreshUserInfo() {
        if (UserUtil.getUserInfo() == null) return;
        if (UserUtil.isLogined()) {
            String username = UserUtil.getUserInfo().data.username;
            if (tv_user_profile_not_login != null)
                tv_user_profile_not_login.setText(username);
            String photoUrl = UserUtil.getUserInfo().data.icon;
            if (photoUrl != null) {
                RequestOptions options = new RequestOptions().placeholder(R.drawable.user_default_photo);
                if(iv_user_photo!=null)
                Glide.with(getActivity()).load(photoUrl).apply(options).into(iv_user_photo);
            }

        } else {

        }

    }

    private void logout() {

        tv_user_profile_not_login.setText(getString(R.string.login_register));
        iv_user_photo.setImageResource(R.drawable.user_default_photo);
        SPUtils.clear(getActivity(), "phone_num");
        SPUtils.clear(getActivity(), "password");
        mPresenter.getLogoutData();
        RegisterLoginActivity.startActivity(getActivity());
        UserUtil.setUserInfo(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(getActivity());
    }
}
