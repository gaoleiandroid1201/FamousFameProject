package com.android.learn.base.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.gaolei.basemodule.R;
import com.android.learn.base.utils.NetUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;




public abstract class BaseFragment extends Fragment implements View.OnClickListener {
    private FrameLayout mLlContent;
    View subFragmentView;
    private Button bt_error_refresh;
    public LinearLayout mErrorPageView;
    private Unbinder mBinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mParentView = inflater.inflate(R.layout.fragment_base, container, false);
        initBaseView(mParentView);
        addContentView(inflater);
        Bundle bundle = getArguments();
        if (bundle == null) {
            bundle = savedInstanceState;
        }
        initData(bundle);
        initView(mParentView);
        return mParentView;
    }

    /**
     * 用于布局加载完毕，子Fragment可以开始初始化数据
     *
     * @param bundle
     */
    public abstract void initData(Bundle bundle);
    public abstract void initView(View view);

    private void initBaseView(View view) {
        mLlContent = view.findViewById(R.id.base_fragment_content);
        mErrorPageView = view.findViewById(R.id.ll_base_error_content);
        bt_error_refresh = view.findViewById(R.id.bt_error_refresh);
//        if (!NetworkUtil.isNetworkAvailable(getActivity()))
//            showErrorPage(true);
        bt_error_refresh.setOnClickListener(this);
    }

    /**
     * 设置子布局layout
     */
    public abstract int setContentLayout();

    public abstract void reload();

    /**
     * 设置内容
     */
    public void addContentView(LayoutInflater inflater) {
        subFragmentView = inflater.inflate(setContentLayout(), null);
        mLlContent.addView(subFragmentView);
        mBinder = ButterKnife.bind(this, subFragmentView);

    }



    /**
     * 显示/隐藏 错误页面
     *
     * @param isShow
     */
    public void showErrorPage(boolean isShow) {
        mErrorPageView.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_error_refresh) {
            if (NetUtils.isConnected())
                mErrorPageView.setVisibility(View.GONE);
            reload();
        }
    }


    public void onDestroyView() {
        super.onDestroyView();
        if (mBinder != null) {
            mBinder.unbind();
        }
    }
}
