package com.android.learn.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class CustomViewPager extends ViewPager {


    public boolean canScroll = true;

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomViewPager(Context context) {
        super(context);
    }


    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return canScroll && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return canScroll && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
       if(getCurrentItem()!=0)
        getParent().requestDisallowInterceptTouchEvent(true);//如果不是viewpager的第一项让父控件不要拦截事件
        return super.dispatchTouchEvent(ev);
    }

//    @Override
//    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
//        if (v != this && v instanceof ViewPager) {   //判断当前的View是不是ViewPager
//            int currentItem = ((ViewPager) v).getCurrentItem();   //当前的条目
//            int countItem = ((ViewPager) v).getAdapter().getCount();  //总的条目
//            if ((currentItem == (countItem - 1) && dx < 0) || (currentItem == 0 && dx > 0)) {   //判断当前条目以及滑动方向
//                return false;
//            }
//            return true;
//        }
//        return super.canScroll(v, checkV, dx, x, y);
//    }

    @Override
    public void setCurrentItem(int item) {
        setCurrentItem(item, false);
    }
}
