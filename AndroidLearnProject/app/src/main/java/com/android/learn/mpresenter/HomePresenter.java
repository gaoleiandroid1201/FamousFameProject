package com.android.learn.mpresenter;


import com.android.learn.base.mmodel.BannerListData;
import com.android.learn.base.mmodel.BaseData;
import com.android.learn.base.mmodel.BaseResponse;
import com.android.learn.base.mmodel.FeedArticleListData.FeedArticleData;
import com.android.learn.base.mmodel.FeedArticleListData;
import com.android.learn.base.mpresenter.BasePresenter;
import com.android.learn.base.thirdframe.rxjava.BaseObserver;
import com.android.learn.base.utils.ResponseStatusUtil;
import com.android.learn.mcontract.HomeContract;

import io.reactivex.Observable;


public class HomePresenter extends BasePresenter<HomeContract.View> implements HomeContract.Presenter {
    private boolean isRefresh = true;
    public int mCurrentPage = 0;

    @Override
    public void onRefreshMore() {
        Observable observable = getMRestService().getFeedArticleList(-1);
        addSubscribe(observable, new BaseObserver<BaseResponse<FeedArticleListData>>(false) {
            @Override
            public void onNext(BaseResponse<FeedArticleListData> feedArticleListData) {
                getMView().showArticleList(feedArticleListData.getData(), true);
            }
        });


    }

    @Override
    public void onLoadMore() {
        ++mCurrentPage;
        Observable observable = getMRestService().getFeedArticleList(mCurrentPage);
        addSubscribe(observable, new BaseObserver<BaseResponse<FeedArticleListData>>(false) {
            @Override
            public void onNext(BaseResponse<FeedArticleListData> feedArticleListData) {
                getMView().showArticleList(feedArticleListData.getData(), false);
            }
        });
    }

    @Override
    public void getFeedArticleList(int num) {
        Observable observable = getMRestService().getFeedArticleList(num);
        addSubscribe(observable, new BaseObserver<BaseResponse<FeedArticleListData>>(true) {
            @Override
            public void onNext(BaseResponse<FeedArticleListData> feedArticleListData) {
                getMView().showArticleList(feedArticleListData.getData(), false);
            }
        });
    }

    @Override
    public void getBannerInfo() {
        Observable observable = getMRestService().getBannerListData();
        addSubscribe(observable, new BaseObserver<BannerListData>(true) {

            @Override
            public void onNext(BannerListData bannerListData) {
                getMView().showBannerList(bannerListData);
            }

        });


    }


    @Override
    public void addCollectArticle(final int position, final FeedArticleData feedArticleData) {
        Observable observable = getMRestService().addCollectArticle(feedArticleData.getId());
        addSubscribe(observable, new BaseObserver<BaseData>(true) {

            @Override
            public void onNext(BaseData data) {
                if (data.errorCode == BaseData.SUCCESS) {
                    feedArticleData.setCollect(true);
                    getMView().showCollectArticleData(position, feedArticleData);
                } else ResponseStatusUtil.handleResponseStatus(data);
            }

        });

    }

    @Override
    public void cancelCollectArticle(final int position, final FeedArticleData feedArticleData) {
        Observable observable = getMRestService().cancelCollectArticle(feedArticleData.getId(), -1);
        addSubscribe(observable, new BaseObserver<BaseData>(true) {

            @Override
            public void onNext(BaseData data) {
                if (data.errorCode == BaseData.SUCCESS) {
                    feedArticleData.setCollect(false);
                    getMView().showCancelCollectArticleData(position, feedArticleData);
                } else ResponseStatusUtil.handleResponseStatus(data);
            }

        });
    }

    public void cancelCollectArticle(final int id) {
        Observable observable = getMRestService().cancelCollectArticle(id, -1);
        addSubscribe(observable, new BaseObserver<BaseData>(true) {

            @Override
            public void onNext(BaseData data) {
                if (data.errorCode == BaseData.SUCCESS) {
                    getMView().showCancelCollectArticleData(id);
                } else ResponseStatusUtil.handleResponseStatus(data);
            }

        });
    }

}
