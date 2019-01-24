package com.android.learn.mpresenter;


import com.android.learn.base.mmodel.BaseData;
import com.android.learn.base.mpresenter.BasePresenter;
import com.android.learn.base.thirdframe.rxjava.BaseObserver;
import com.android.learn.base.utils.ResponseStatusUtil;
import com.android.learn.mcontract.TodoAddContract;
import com.android.learn.mcontract.TodoEditContract;

import java.util.Map;

import io.reactivex.Observable;


public class TodoAddPresenter extends BasePresenter<TodoAddContract.View> implements TodoAddContract.Presenter {

    @Override
    public void addTodo( Map<String, Object> map) {
        Observable observable = mRestService.addTodo(map);

        addSubscribe(observable, new BaseObserver<BaseData>(false) {

            @Override
            public void onNext(BaseData data) {
                if (data.errorCode == BaseData.SUCCESS) {
                    mView.showAddTodo(data);
                } else ResponseStatusUtil.handleResponseStatus(data);
            }
        });
    }

}
