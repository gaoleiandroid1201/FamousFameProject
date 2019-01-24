package com.android.learn.mpresenter;

import com.android.learn.base.mmodel.BaseData;
import com.android.learn.base.mmodel.RegisterLoginData;
import com.android.learn.base.mpresenter.BasePresenter;
import com.android.learn.base.thirdframe.retrofit.ApiService;
import com.android.learn.base.thirdframe.retrofit.RetrofitProvider;
import com.android.learn.base.thirdframe.rxjava.BaseObserver;
import com.android.learn.base.utils.ResponseStatusUtil;
import com.android.learn.mcontract.SplashLoginContract;

import io.reactivex.Observable;


public class SplashLoginPresenter extends BasePresenter<SplashLoginContract.View> implements SplashLoginContract.Presenter {

    @Override
    public void login(String account, String password) {
        Observable observable = RetrofitProvider.getInstance().createService(ApiService.class).login(account, password);
        addSubscribe(observable, new BaseObserver<RegisterLoginData>(false) {
            @Override
            public void onNext(RegisterLoginData data) {
                    getMView().showLoginResData(data);

            }
        });
    }
}
