package com.android.learn.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.android.learn.R;
import com.android.learn.adapter.HorizontalPagerAdapter;
import com.android.learn.base.activity.BaseActivity;
import com.android.learn.base.fragment.BaseMvpFragment;
import com.android.learn.base.mmodel.TreeBean;
import com.android.learn.base.thirdframe.rxjava.BaseObserver;
import com.android.learn.base.view.CustomProgressDialog;
import com.android.learn.mcontract.KnowledgeContract;
import com.android.learn.mpresenter.KnowledgePresenter;
import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;

import java.util.List;

import butterknife.BindView;


public class KnowledgeFragment extends BaseMvpFragment<KnowledgePresenter> implements KnowledgeContract.View {
    @BindView(R.id.hicvp)
    HorizontalInfiniteCycleViewPager horizontalInfiniteCycleViewPager;
    private HorizontalPagerAdapter mPagerAdapter;

    Handler handler = new Handler() {
        public void handleMessage(Message message) {
            CustomProgressDialog.show(getActivity());
        }
    };

    @Override
    public void initData(Bundle bundle) {
    }

    @Override
    public void initView(View view) {
    }

    @Override
    public int setContentLayout() {
        return R.layout.fragment_knowledge;
    }

    @Override
    public void reload() {
    }

    @Override
    public KnowledgePresenter initPresenter() {
        return new KnowledgePresenter();
    }

    @Override
    protected void loadData() {

        CustomProgressDialog.show(getActivity());
        mPresenter.getKnowledge();
    }

    @Override
    public void showKnowledge(List<TreeBean> datas) {
        mPagerAdapter = new HorizontalPagerAdapter(getContext(), datas);
        horizontalInfiniteCycleViewPager.setAdapter(mPagerAdapter);
        if (horizontalInfiniteCycleViewPager != null && datas.size() > 1)
            horizontalInfiniteCycleViewPager.setCurrentItem(1);
        horizontalInfiniteCycleViewPager.post(new Runnable() {
            @Override
            public void run() {
                CustomProgressDialog.cancel();
            }
        });

    }
}
