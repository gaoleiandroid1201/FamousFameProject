package com.android.learn.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.android.learn.R;
import com.android.learn.base.activity.BaseMvpActivity;
import com.android.learn.base.mpresenter.BasePresenter;
import com.android.learn.fragment.TodoFragment;
import com.android.learn.view.CustomViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MyTodoActivity extends BaseMvpActivity {
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.iv_search)
    ImageView iv_search;
    @BindView(R.id.title)
    TextView tv_title;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    CustomViewPager viewPager;
    TodoFragment todoFragment, doneFragment;

    private List<Fragment> mFragments = new ArrayList<>();

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MyTodoActivity.class);
        context.startActivity(intent);
    }

    private void initTab() {

        tabLayout.getTabAt(0).setCustomView(R.layout.tab_todo);
        tabLayout.getTabAt(1).setCustomView(R.layout.tab_done);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            //标签选中之后执行的方法
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//                title.setText(titles.get(tab.getPosition()));
                if (tab.getPosition() == 0)
                    iv_search.setVisibility(View.VISIBLE);
                if (tab.getPosition() == 1)
                    iv_search.setVisibility(View.GONE);

            }

            //标签没选中
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //默认选中的Tab
        tabLayout.getTabAt(0).getCustomView().setSelected(true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_todo;
    }

    @Override
    protected void initData(Bundle bundle) {
        tv_title.setText(getString(R.string.todo));
        iv_back.setVisibility(View.VISIBLE);
        iv_search.setVisibility(View.VISIBLE);
        iv_search.setImageResource(R.drawable.add_todo);
        Bundle bundle1 = new Bundle();
        bundle1.putInt("position", 0);
        todoFragment = TodoFragment.newInstance(bundle1);
        Bundle bundle2 = new Bundle();
        bundle2.putInt("position", 1);
        doneFragment = TodoFragment.newInstance(bundle2);
        mFragments.add(todoFragment);
        mFragments.add(doneFragment);
        CourseDiscussAdapter adapter = new CourseDiscussAdapter(getSupportFragmentManager(), mFragments);
        viewPager.setOffscreenPageLimit(mFragments.size());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        //将TabLayout和ViewPager关联起来
        tabLayout.setupWithViewPager(viewPager);
        initTab();
    }

    @OnClick({R.id.iv_search})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.iv_search:
                TodoAddActivity.startActivity(MyTodoActivity.this, null);
                break;

        }

    }

    @Override
    public BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected void loadData() {

    }

    public class CourseDiscussAdapter extends FragmentPagerAdapter {

        List<Fragment> fragments;

        public CourseDiscussAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;


        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = fragments.get(position);

            return fragment;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
