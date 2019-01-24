package com.android.learn.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import com.android.learn.fragment.HomeFragment;

import java.util.List;


public class MainTabAdapter extends FragmentPagerAdapter {

    List<Fragment> fragments;

    public MainTabAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;


    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = fragments.get(position);
//        Bundle bundle=new Bundle();
//        bundle.putString("title",titles[position]);
//        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}

