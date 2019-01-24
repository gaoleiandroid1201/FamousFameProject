package com.android.learn.mpresenter;


import com.android.learn.base.mmodel.BaseData;
import com.android.learn.base.mmodel.BaseResponse;
import com.android.learn.base.mmodel.TodoData;
import com.android.learn.base.mpresenter.BasePresenter;
import com.android.learn.base.thirdframe.rxjava.BaseObserver;
import com.android.learn.base.utils.ResponseStatusUtil;
import com.android.learn.mcontract.TodoContract;
import com.android.learn.mcontract.TodoEditContract;

import java.util.Map;

import io.reactivex.Observable;


public class TodoEditPresenter extends BasePresenter<TodoEditContract.View> implements TodoEditContract.Presenter {

    @Override
    public void updateTodo(int id, Map<String, Object> map) {
        Observable observable = getMRestService().updateTodo(id, map);

        addSubscribe(observable, new BaseObserver<BaseData>(false) {

            @Override
            public void onNext(BaseData data) {
                if (data.errorCode == BaseData.SUCCESS) {
                    getMView().showUpdateTodo(data);
                } else ResponseStatusUtil.handleResponseStatus(data);
            }
        });
    }

}
