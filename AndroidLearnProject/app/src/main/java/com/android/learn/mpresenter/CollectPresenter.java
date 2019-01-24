package com.android.learn.mpresenter;


import com.android.learn.base.mmodel.BaseData;
import com.android.learn.base.mmodel.BaseResponse;
import com.android.learn.base.mmodel.FeedArticleListData;
import com.android.learn.base.mmodel.FeedArticleListData.FeedArticleData;
import com.android.learn.base.mpresenter.BasePresenter;
import com.android.learn.base.thirdframe.rxjava.BaseObserver;
import com.android.learn.base.utils.ResponseStatusUtil;
import com.android.learn.mcontract.CollectContract;

import io.reactivex.Observable;


public class CollectPresenter extends BasePresenter<CollectContract.View> implements CollectContract.Presenter {

    int page = 0;

    @Override
    public void getCollectList() {

        Observable observable = getMRestService().getCollectList(page);
        addSubscribe(observable, new BaseObserver<BaseResponse<FeedArticleListData>>(true) {

            @Override
            public void onNext(BaseResponse<FeedArticleListData> data) {
                if (data.errorCode == BaseData.SUCCESS) {
                    getMView().showCollectList(data.getData());
                } else ResponseStatusUtil.handleResponseStatus(data);
            }

        });
        page++;
    }

    @Override
    public void cancelCollectArticle(final int position, final int id) {
//        int id = feedArticleData.getId();
        Observable observable = getMRestService().cancelCollectArticle(id, -1);
        addSubscribe(observable, new BaseObserver<BaseData>(true) {

            @Override
            public void onNext(BaseData data) {
                if (data.errorCode == BaseData.SUCCESS) {
                    getMView().showCancelCollectArticle(position, id);
                } else ResponseStatusUtil.handleResponseStatus(data);
            }

        });
    }
}
