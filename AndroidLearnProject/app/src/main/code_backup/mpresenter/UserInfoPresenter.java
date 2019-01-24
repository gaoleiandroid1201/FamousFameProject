package com.android.learn.mpresenter;

import com.android.learn.base.mmodel.BaseData;
import com.android.learn.base.mpresenter.BasePresenter;
import com.android.learn.base.thirdframe.retrofit.ApiService;
import com.android.learn.base.thirdframe.retrofit.RetrofitProvider;
import com.android.learn.base.thirdframe.rxjava.BaseObserver;
import com.android.learn.mcontract.UserInfoContract;

import io.reactivex.Observable;

public class UserInfoPresenter extends BasePresenter<UserInfoContract.View> implements UserInfoContract.Presenter {

    @Override
    public void getLogoutData() {

        Observable observable = RetrofitProvider.getInstance().createService(ApiService.class).logout();
        addSubscribe(observable, new BaseObserver<BaseData>(false) {
            @Override
            public void onNext(BaseData baseData) {
            }
        });
    }


}
