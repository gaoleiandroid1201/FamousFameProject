package com.android.learn.mpresenter;

import com.android.learn.base.mmodel.BaseResponse;
import com.android.learn.base.mmodel.TreeBean;
import com.android.learn.base.mpresenter.BasePresenter;
import com.android.learn.base.thirdframe.rxjava.BaseObserver;
import com.android.learn.mcontract.KnowledgeContract;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;


/**
 * <pre>
 *     author : lex
 *     e-mail : ldlywt@163.com
 *     time   : 2018/09/05
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class KnowledgePresenter extends BasePresenter<KnowledgeContract.View> implements KnowledgeContract.Presenter {

    @Override
    public void getKnowledge() {

        Observable observable = getMRestService().getKnowledgeTree();
        BaseObserver baseObserver=new BaseObserver<BaseResponse<List<TreeBean>>>(false) {
            @Override
            public void onNext(BaseResponse<List<TreeBean>> datas) {
                getMView().showKnowledge(datas.data);
            }
        };
        baseObserver.setCancelDialog(false);
        addSubscribe(observable, baseObserver);


    }




}
