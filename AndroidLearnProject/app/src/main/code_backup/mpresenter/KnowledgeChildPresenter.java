package com.android.learn.mpresenter;

import com.android.learn.base.mmodel.BaseData;
import com.android.learn.base.mmodel.BaseResponse;
import com.android.learn.base.mmodel.FeedArticleListData;
import com.android.learn.base.mmodel.FeedArticleListData.FeedArticleData;
import com.android.learn.base.mpresenter.BasePresenter;
import com.android.learn.base.thirdframe.rxjava.BaseObserver;
import com.android.learn.base.utils.ResponseStatusUtil;
import com.android.learn.mcontract.KnowledgeChildContract;

import io.reactivex.Observable;


public class KnowledgeChildPresenter extends BasePresenter<KnowledgeChildContract.View> implements KnowledgeChildContract.Presenter {
    private int mCurrentPage = 0;

    @Override
    public void getKnowledgeArticleList(int num, int cid) {
        Observable observable = mRestService.getKnowledgeArticleList(num, cid);
        addSubscribe(observable, new BaseObserver<BaseResponse<FeedArticleListData>>(true) {
            @Override
            public void onNext(BaseResponse<FeedArticleListData> feedArticleListData) {
                mView.showArticleList(feedArticleListData.getData(), false);
            }
        });
    }

    @Override
    public void onRefreshMore(int cid) {
        Observable observable = mRestService.getKnowledgeArticleList(-1, cid);
        addSubscribe(observable, new BaseObserver<BaseResponse<FeedArticleListData>>(false) {
            @Override
            public void onNext(BaseResponse<FeedArticleListData> feedArticleListData) {
                mView.showArticleList(feedArticleListData.getData(), true);
            }
        });


    }

    @Override
    public void onLoadMore(int cid) {
        ++mCurrentPage;
        Observable observable = mRestService.getKnowledgeArticleList(mCurrentPage, cid);
        addSubscribe(observable, new BaseObserver<BaseResponse<FeedArticleListData>>(false) {
            @Override
            public void onNext(BaseResponse<FeedArticleListData> feedArticleListData) {
                mView.showArticleList(feedArticleListData.getData(), false);
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


}
