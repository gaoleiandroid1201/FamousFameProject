package com.android.learn.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.android.learn.R;
import com.android.learn.base.fragment.BaseMvpFragment;
import com.android.learn.base.mmodel.FeedArticleListData;
import com.android.learn.base.mmodel.WxArticle;
import com.android.learn.base.view.colorfultab.ColorClipTabLayout;
import com.android.learn.mcontract.WechatContract;
import com.android.learn.mpresenter.WechatPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class WechatFragment extends BaseMvpFragment<WechatPresenter> implements WechatContract.View {

    @BindView(R.id.tab_layout)
    ColorClipTabLayout tab_layout;
    @BindView(R.id.view_pager)
    ViewPager view_pager;

    List<Fragment> fragmentList = new ArrayList();
    List<String> titleList = new ArrayList();

    @Override
    protected void loadData() {
        mPresenter.getWxArticle();
    }

    @Override
    public void initData(Bundle bundle) {

    }

    @Override
    public void initView(View view) {

    }

    @Override
    public int setContentLayout() {
        return R.layout.fragment_wechat;
    }

    @Override
    public void reload() {
    }

    @Override
    public WechatPresenter initPresenter() {
        return new WechatPresenter();
    }

    @Override
    public void showWxArticle(List<WxArticle> list) {
        WechatSubFragment firstSubFragment = null;
        int firstId = 0;
        int size = list.size();
        for (int i = 0; i < size; i++) {
            WxArticle wxArticle = list.get(i);
            titleList.add(wxArticle.getName());
            WechatSubFragment wechatSubFragment = WechatSubFragment.newInstance(wxArticle.getId(), wxArticle.getName());
            fragmentList.add(wechatSubFragment);
            if (i == 0) {
                firstId = wxArticle.getId();
                firstSubFragment = wechatSubFragment;
            }
        }
        CustomPagerAdapter adapter = new CustomPagerAdapter(getFragmentManager(), fragmentList);
        view_pager.setAdapter(adapter);
        view_pager.setOffscreenPageLimit(list.size());
        tab_layout.setupWithViewPager(view_pager);
        firstSubFragment.userId = firstId;
        firstSubFragment.loadData();
    }


    public class CustomPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments;

        public CustomPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.mFragments = fragments;
            fm.beginTransaction().commitAllowingStateLoss();
        }

        @Override
        public Fragment getItem(int position) {
            return this.mFragments.get(position);
        }

        @Override
        public int getCount() {
            return this.mFragments.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }
    }
}
