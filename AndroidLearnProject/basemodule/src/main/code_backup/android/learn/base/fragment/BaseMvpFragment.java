package com.android.learn.base.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.learn.base.mpresenter.BasePresenter;


/**
 * Created by gaolei on 2018/4/26.
 */


public abstract class BaseMvpFragment<P extends BasePresenter> extends BaseFragment {

    public P mPresenter;
    //Fragment的View加载完毕的标记
    private boolean isViewCreated;
    //Fragment对用户可见的标记
    private boolean isUIVisible;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isViewCreated = true;

        mPresenter = initPresenter();
        if (mPresenter != null)
            mPresenter.attach(this);
        lazyLoad();
    }

    @Override
    public void onDestroy() {
        if (mPresenter != null)
            mPresenter.dettach();
        super.onDestroy();
    }

    //实例presenter
    public abstract P initPresenter();


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);
        //isVisibleToUser这个boolean值表示:该Fragment的UI 用户是否可见
        if (isVisibleToUser) {
            isUIVisible = true;
            lazyLoad();
        } else {
            isUIVisible = false;
        }
    }

    private void lazyLoad() {

        //这里进行双重标记判断,是因为setUserVisibleHint会多次回调,并且会在onCreateView执行前回调,必须确保onCreateView加载完毕且页面可见,才加载数据
        if (isViewCreated && isUIVisible) {

            loadData();
            //数据加载完毕,恢复标记,防止重复加载
            isViewCreated = false;
            isUIVisible = false;

        }
    }


    protected abstract void loadData();


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //页面销毁,恢复标记
        isViewCreated = false;
        isUIVisible = false;

    }
}
