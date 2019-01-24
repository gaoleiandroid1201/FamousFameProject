package com.android.learn.mcontract;

import com.android.learn.base.mmodel.RegisterLoginData;

/**
 * Created by gaolei on 2018/6/18.
 */

public class RegisterLoginContract {

    public interface Presenter {

        void login(String account, String password);

        void register(String account, String password, String rePassword);

    }

    public interface View {

        void showRegisterResData(RegisterLoginData registerResData);

        void showLoginResData(RegisterLoginData loginData);
    }
}
