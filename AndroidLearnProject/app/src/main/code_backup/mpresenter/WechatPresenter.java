package com.android.learn.mpresenter;


import com.android.learn.base.mmodel.BannerListData;
import com.android.learn.base.mmodel.BaseData;
import com.android.learn.base.mmodel.BaseResponse;
import com.android.learn.base.mmodel.FeedArticleListData;
import com.android.learn.base.mmodel.FeedArticleListData.FeedArticleData;
import com.android.learn.base.mmodel.WxArticle;
import com.android.learn.base.mpresenter.BasePresenter;
import com.android.learn.base.thirdframe.rxjava.BaseObserver;
import com.android.learn.base.utils.ResponseStatusUtil;
import com.android.learn.mcontract.HomeContract;
import com.android.learn.mcontract.WechatContract;

import java.util.List;

import io.reactivex.Observable;


public class WechatPresenter extends BasePresenter<WechatContract.View> implements WechatContract.Presenter {

    @Override
    public void getWxArticle() {
        Observable observable = mRestService.getWxArticle();
        addSubscribe(observable, new BaseObserver<BaseResponse<List<WxArticle>>>(false) {
            @Override
            public void onNext(BaseResponse<List<WxArticle>> datas) {
                mView.showWxArticle(datas.data);
            }
        });
    }

}
