package com.android.learn.mcontract;

import com.android.learn.base.mmodel.RegisterLoginData;

/**
 * Created by gaolei on 2018/6/18.
 */

public class SplashLoginContract {

    public interface Presenter {


        void login(String username, String password);


    }

    public interface View {

        void showLoginResData(RegisterLoginData loginResData);

    }

}
