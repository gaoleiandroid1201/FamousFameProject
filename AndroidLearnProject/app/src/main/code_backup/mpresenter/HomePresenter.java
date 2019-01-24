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
        Observable observable = mRestService.getFeedArticleList(-1);
        addSubscribe(observable, new BaseObserver<BaseResponse<FeedArticleListData>>(false) {
            @Override
            public void onNext(BaseResponse<FeedArticleListData> feedArticleListData) {
                mView.showArticleList(feedArticleListData.getData(), true);
            }
        });


    }

    @Override
    public void onLoadMore() {
        ++mCurrentPage;
        Observable observable = mRestService.getFeedArticleList(mCurrentPage);
        addSubscribe(observable, new BaseObserver<BaseResponse<FeedArticleListData>>(false) {
            @Override
            public void onNext(BaseResponse<FeedArticleListData> feedArticleListData) {
                mView.showArticleList(feedArticleListData.getData(), false);
            }
        });
    }

    @Override
    public void getFeedArticleList(int num) {
        Observable observable = mRestService.getFeedArticleList(num);
        addSubscribe(observable, new BaseObserver<BaseResponse<FeedArticleListData>>(true) {
            @Override
            public void onNext(BaseResponse<FeedArticleListData> feedArticleListData) {
                mView.showArticleList(feedArticleListData.getData(), false);
            }
        });
    }

    @Override
    public void getBannerInfo() {
        Observable observable = mRestService.getBannerListData();
        addSubscribe(observable, new BaseObserver<BannerListData>(true) {

            @Override
            public void onNext(BannerListData bannerListData) {
                mView.showBannerList(bannerListData);
            }

        });


    }


    @Override
    public void addCollectArticle(final int position, final FeedArticleData feedArticleData) {
        Observable observable = mRestService.addCollectArticle(feedArticleData.getId());
        addSubscribe(observable, new BaseObserver<BaseData>(true) {

            @Override
            public void onNext(BaseData data) {
                if (data.errorCode == BaseData.SUCCESS) {
                    feedArticleData.setCollect(true);
                    mView.showCollectArticleData(position, feedArticleData);
                } else ResponseStatusUtil.handleResponseStatus(data);
            }

        });

    }

    @Override
    public void cancelCollectArticle(final int position, final FeedArticleData feedArticleData) {
        Observable observable = mRestService.cancelCollectArticle(feedArticleData.getId(), -1);
        addSubscribe(observable, new BaseObserver<BaseData>(true) {

            @Override
            public void onNext(BaseData data) {
                if (data.errorCode == BaseData.SUCCESS) {
                    feedArticleData.setCollect(false);
                    mView.showCancelCollectArticleData(position, feedArticleData);
                } else ResponseStatusUtil.handleResponseStatus(data);
            }

        });
    }

    public void cancelCollectArticle(final int id) {
        Observable observable = mRestService.cancelCollectArticle(id, -1);
        addSubscribe(observable, new BaseObserver<BaseData>(true) {

            @Override
            public void onNext(BaseData data) {
                if (data.errorCode == BaseData.SUCCESS) {
                    mView.showCancelCollectArticleData(id);
                } else ResponseStatusUtil.handleResponseStatus(data);
            }

        });
    }

}
