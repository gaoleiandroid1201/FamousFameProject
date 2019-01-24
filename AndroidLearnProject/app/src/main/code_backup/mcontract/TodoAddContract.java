package com.android.learn.mcontract;

import com.android.learn.base.mmodel.BaseData;

import java.util.Map;

/**
 * Created by gaolei on 2018/6/18.
 */

public class TodoAddContract {

    public interface Presenter {

        void addTodo( Map<String, Object> map);

    }

    public interface View {


        void showAddTodo(BaseData todoData);
    }
}
