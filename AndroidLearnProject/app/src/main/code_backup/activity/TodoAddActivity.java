package com.android.learn.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.learn.R;
import com.android.learn.base.activity.BaseMvpActivity;
import com.android.learn.base.event.UpdateTodoEvent;
import com.android.learn.base.mmodel.BaseData;
import com.android.learn.base.mmodel.TodoData.DatasBean;
import com.android.learn.base.utils.LanguageUtil;
import com.android.learn.base.utils.Utils;
import com.android.learn.mcontract.TodoAddContract;
import com.android.learn.mcontract.TodoEditContract;
import com.android.learn.mpresenter.TodoAddPresenter;
import com.android.learn.mpresenter.TodoEditPresenter;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;


public class TodoAddActivity extends BaseMvpActivity<TodoAddPresenter> implements TodoAddContract.View {
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.todo_name)
    TextInputEditText et_title;
    @BindView(R.id.todo_des)
    TextInputEditText et_content;
    @BindView(R.id.todo_date)
    TextView mTodoDate;

    public static void startActivity(Context context, Bundle bundle) {
        Intent intent = new Intent(context, TodoAddActivity.class);
        if(bundle!=null)
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_todo;
    }

    @Override
    protected void initData(Bundle bundle) {
        mTitle.setText(getString(R.string.add));
        iv_back.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.save_todo,R.id.todo_date})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.save_todo:
                Map<String,Object> map=new HashMap<>();
                map.put("title",et_title.getText().toString());
                map.put("content",et_content.getText().toString());
                String date=mTodoDate.getText().toString();
                if(!date.contains("-")){
                    Utils.showToast(getString(R.string.select_time),true);
                    return;
                }
                map.put("date",date);
//                map.put("status",0);
                map.put("type",0);

                mPresenter.addTodo(map);
                break;
            case R.id.todo_date:
                Calendar calendar = Calendar.getInstance();

                DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        mTodoDate.setText(String.format("%d-%d-%d", year, month + 1, dayOfMonth));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
                datePickerDialog.show();
                break;

        }

    }


    @Override
    public TodoAddPresenter initPresenter() {
        return new TodoAddPresenter();
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void showAddTodo(BaseData todoData) {
        Utils.showToast(getString(R.string.operate_success),true);
        EventBus.getDefault().post(new UpdateTodoEvent());
        finish();
    }
}
