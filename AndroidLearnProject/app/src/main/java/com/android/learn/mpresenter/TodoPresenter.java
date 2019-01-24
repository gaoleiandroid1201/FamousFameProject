package com.android.learn.mpresenter;


import com.android.learn.base.mmodel.BaseData;
import com.android.learn.base.mmodel.BaseResponse;
import com.android.learn.base.mmodel.TodoData;
import com.android.learn.base.mpresenter.BasePresenter;
import com.android.learn.base.thirdframe.rxjava.BaseObserver;
import com.android.learn.base.utils.ResponseStatusUtil;
import com.android.learn.mcontract.TodoContract;

import java.util.Map;

import io.reactivex.Observable;


public class TodoPresenter extends BasePresenter<TodoContract.View> implements TodoContract.Presenter {
    public int notDonePage = 1, donePage = 1;

    @Override
    public void getListNotDone(int type) {
        Observable observable = getMRestService().getListNotDone(type, notDonePage);
        addSubscribe(observable, new BaseObserver<BaseResponse<TodoData>>(true) {

            @Override
            public void onNext(BaseResponse<TodoData> data) {
                if (data.errorCode == BaseData.SUCCESS) {
                    getMView().showListNotDone(data.getData());
                } else ResponseStatusUtil.handleResponseStatus(data);
            }

        });
        notDonePage++;
    }

    @Override
    public void getListDone(int type) {
        Observable observable = getMRestService().getListDone(type, donePage);
        addSubscribe(observable, new BaseObserver<BaseResponse<TodoData>>(true) {

            @Override
            public void onNext(BaseResponse<TodoData> data) {
                if (data.errorCode == BaseData.SUCCESS) {
                    getMView().showListDone(data.getData());
                } else ResponseStatusUtil.handleResponseStatus(data);
            }

        });
        donePage++;
    }

    @Override
    public void deleteTodo(int id) {
        Observable observable = getMRestService().deleteTodo(id);

        addSubscribe(observable, new BaseObserver<BaseData>(false) {

            @Override
            public void onNext(BaseData data) {
                if (data.errorCode == BaseData.SUCCESS) {
                } else ResponseStatusUtil.handleResponseStatus(data);
            }
        });
    }
    //status: 0或1，传1代表未完成到已完成
    @Override
    public void updateTodoStatus(int id, int status) {
        Observable observable = getMRestService().updateTodoStatus(id, status);

        addSubscribe(observable, new BaseObserver<BaseData>(false) {

            @Override
            public void onNext(BaseData data) {
                if (data.errorCode == BaseData.SUCCESS) {
                    getMView().showUpdateTodoStatus(data);
                } else ResponseStatusUtil.handleResponseStatus(data);
            }
        });
    }
}
