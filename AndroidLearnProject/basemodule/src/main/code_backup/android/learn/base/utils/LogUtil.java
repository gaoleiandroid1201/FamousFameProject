/**
 * Copyright (C) 2006-2014 Tuniu All rights reserved
 */
package com.android.learn.base.utils;

import android.util.Log;

import com.gaolei.basemodule.BuildConfig;


public class LogUtil {

    private static boolean isDebug = BuildConfig.DEBUG;
    private static final String TAG = "zmy";

    public static void init(boolean isPrintable) {
        isDebug = isPrintable;
    }

    public static void d(String msg) {
        if (!isDebug) {
            return;
        }
        Log.e(TAG, msg);
    }

    public static void v(String tag, String msg) {
        if (!isDebug) {
            return;
        }
        Log.v(tag, msg);
    }


    public static void d(String tag, String msg) {
        if (!isDebug) {
            return;
        }
        Log.d(tag, msg);
    }


    public static void i(String tag, String msg) {
        if (!isDebug) {
            return;
        }
        Log.i(tag, msg);
    }


    public static void w(String tag, String msg) {
        if (!isDebug) {
            return;
        }
        Log.w(tag, msg);
    }


    public static void e(String tag, String msg) {
        if (!isDebug) {
            return;
        }
        Log.e(tag, msg);
    }


}
