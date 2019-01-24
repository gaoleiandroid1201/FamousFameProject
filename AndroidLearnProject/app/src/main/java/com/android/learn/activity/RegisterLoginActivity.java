package com.android.learn.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.learn.R;
import com.android.learn.base.activity.BaseMvpActivity;
import com.android.learn.base.mmodel.RegisterLoginData;
import com.android.learn.base.utils.LanguageUtil;
import com.android.learn.base.utils.SPUtils;
import com.android.learn.base.utils.Utils;
import com.android.learn.base.utils.account.UserUtil;
import com.android.learn.mcontract.RegisterLoginContract;
import com.android.learn.mpresenter.RegisterLoginPresenter;

import butterknife.BindView;
import butterknife.OnClick;


public class RegisterLoginActivity extends BaseMvpActivity<RegisterLoginPresenter> implements RegisterLoginContract.View {
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.tv_login)
    TextView tv_login;
    @BindView(R.id.tv_register_free)
    TextView tv_register_free;

    @BindView(R.id.tv_register)
    TextView tv_register;

    @BindView(R.id.layout_login)
    LinearLayout layout_login;
    @BindView(R.id.layout_register)
    LinearLayout layout_register;


    @BindView(R.id.et_register_phone_num)
    EditText et_register_phone_num;

    @BindView(R.id.et_register_password)
    EditText et_register_password;
    @BindView(R.id.et_register_confirm_password)
    EditText et_register_confirm_password;


    @BindView(R.id.et_login_phone_num)
    EditText et_login_phone_num;
    @BindView(R.id.et_login_password)
    EditText et_login_password;

    TextView tv_register_contract_content;
    boolean isInContract;

    boolean isInRegister = false, isInForgetPwd = false;
    String registerFlag;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, RegisterLoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_register;
    }

    @Override
    protected void initData(Bundle bundle) {
        title.setText(getString(R.string.login));
        iv_back.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.iv_back, R.id.tv_login, R.id.tv_register_free, R.id.tv_register})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                back();
                break;
            case R.id.tv_login:
                String username = et_login_phone_num.getText().toString();
                String password = et_login_password.getText().toString();
                getMPresenter().login(username, password);
                break;
            case R.id.tv_register_free:
                layout_register.setVisibility(View.VISIBLE);
                title.setText(getString(R.string.register));
                isInRegister = true;
                break;

            case R.id.tv_register:
                registerFlag = "tv_register";

                username = et_register_phone_num.getText().toString().trim();
                password = et_register_password.getText().toString().trim();
                String confirmPassword = et_register_confirm_password.getText().toString().trim();

                if ("".equals(username)) {
                    Utils.showToast(getString(R.string.please_input_account), true);
                    return;
                }
                if (password.length() < 6) {
                    Utils.showToast(getString(R.string.register_password_too_short), true);
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    Utils.showToast(getString(R.string.password_dismatch), true);
                    return;
                }

                getMPresenter().register(username, password, confirmPassword);
                break;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            back();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    private void back() {
        if (isInRegister) {
            layout_register.setVisibility(View.GONE);
            title.setText(getString(R.string.login));
            isInRegister = false;
        } else {
            finish();

        }
    }


    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public RegisterLoginPresenter initPresenter() {
        return new RegisterLoginPresenter();
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void showRegisterResData(RegisterLoginData registerResData) {
        back();
    }

    @Override
    public void showLoginResData(RegisterLoginData data) {
        UserUtil.setUserInfo(data);
        SPUtils.setParam(RegisterLoginActivity.this, "username", et_login_phone_num.getText().toString());
        SPUtils.setParam(RegisterLoginActivity.this, "password", et_login_password.getText().toString());
        finish();
    }
}
