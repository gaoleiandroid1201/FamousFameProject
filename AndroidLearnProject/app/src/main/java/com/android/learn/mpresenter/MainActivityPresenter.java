package com.android.learn.mpresenter;


import com.android.learn.base.mmodel.BaseData;
import com.android.learn.base.mmodel.BaseResponse;
import com.android.learn.base.mmodel.FeedArticleListData;
import com.android.learn.base.mmodel.FeedArticleListData.FeedArticleData;
import com.android.learn.base.mmodel.HotKeyData;
import com.android.learn.base.mmodel.TreeBean;
import com.android.learn.base.mpresenter.BasePresenter;
import com.android.learn.base.thirdframe.rxjava.BaseObserver;
import com.android.learn.base.utils.ResponseStatusUtil;
import com.android.learn.mcontract.CollectContract;
import com.android.learn.mcontract.MainActivityContract;

import java.util.List;

import io.reactivex.Observable;


public class MainActivityPresenter extends BasePresenter<MainActivityContract.View> implements MainActivityContract.Presenter {

    private int mCurrentPage = 0;

    @Override
    public void getHotKey() {
        Observable observable = getMRestService().getHotKey();
        addSubscribe(observable, new BaseObserver<BaseResponse<List<HotKeyData>>>(true) {

            @Override
            public void onNext(BaseResponse<List<HotKeyData>> datas) {
                if (datas.errorCode == BaseData.SUCCESS) {
                    getMView().showHotKey(datas.data);
                } else ResponseStatusUtil.handleResponseStatus(datas);
            }

        });

    }

}
