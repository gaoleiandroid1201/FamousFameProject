package com.android.learn.base.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;
import android.util.Log;


import java.util.Locale;

public class LanguageUtil {

    private static final String TAG = "LanguageUtil";


    /**
     * 获取选择的语言设置
     *
     * @param context
     * @return
     */
    public static Locale getSetLanguageLocale(Context context) {
        int language = (Integer) SPUtils.getParam(context, "language", 0);
        LogUtil.d(TAG, "language---------------" + language);
//        LogUtil.d(TAG, "getSystemLocale(context)---------------" + getSystemLocale(context));
        switch (language) {
            case 0:
                return getSystemLocale(context);
            case 1:
                return Locale.CHINA;
            case 2:
                return Locale.ENGLISH;
            default:
                return Locale.CHINA;
        }
    }

    public static void saveSelectLanguage(Context context, int select) {
        SPUtils.setParam(context, "language", select);

        setApplicationLanguage(context);
    }

    public static Context setLocal(Context context) {
        LogUtil.d(TAG, "getSetLanguageLocale---------------" + getSetLanguageLocale(context));
        return updateResources(context, getSetLanguageLocale(context));
    }

    private static Context updateResources(Context context, Locale locale) {
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale);
            context = context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        return context;
    }

    /**
     * 我们都会在代码中调用context.getResource().getString()这句代码看起来没什么问题，但是你这个context要是用的是applicationContext那么问题就来了。
     * 你会发现当你切换语言后用这样方式设置的string没有改变，所以我们需要改动我们的代码。
     解决方法就是，在切换语言后把application的updateConfiguration也要更新了
     */
    public static void setApplicationLanguage(Context context) {
        Resources resources = context.getApplicationContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        Locale locale = getSetLanguageLocale(context);
        config.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);
            context.getApplicationContext().createConfigurationContext(config);
            Locale.setDefault(locale);
        }
        resources.updateConfiguration(config, dm);
    }

    public static Locale getSystemLocale(Context context) {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        return locale;
    }

//    public static void saveSystemCurrentLanguage(Context context) {
//        Locale locale;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            locale = LocaleList.getDefault().get(0);
//        } else {
//            locale = Locale.getDefault();
//        }
//        Log.d(TAG, locale.getLanguage());
//        SPUtil.getInstance(context).setSystemCurrentLocal(locale);
//    }

//    public static void onConfigurationChanged(Context context){
////        saveSystemCurrentLanguage(context);
//        setLocal(context);
//        setApplicationLanguage(context);
//    }
}
