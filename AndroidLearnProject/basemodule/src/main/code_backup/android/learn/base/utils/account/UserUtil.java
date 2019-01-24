package com.android.learn.base.utils.account;

import com.android.learn.base.event.LoginEvent;
import com.android.learn.base.mmodel.RegisterLoginData;

import org.greenrobot.eventbus.EventBus;

public class UserUtil {

    public static boolean logined = false;
    public static RegisterLoginData userInfo;

    public static boolean isLogined() {
        return logined;
    }

    public static void setLogined(boolean logined) {
        UserUtil.logined = logined;
    }

    public static RegisterLoginData getUserInfo() {
        return userInfo;
    }

    public static void setUserInfo(RegisterLoginData userInfo) {
        UserUtil.userInfo = userInfo;
        if (userInfo != null) {
            setLogined(true);
        } else setLogined(false);
        LoginEvent accountEvent = new LoginEvent();
        EventBus.getDefault().post(accountEvent);

    }
}
