package com.android.learn.base.utils;

import android.app.ActivityManager;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;


import com.android.learn.base.application.CustomApplication;

import java.security.MessageDigest;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by gaolei on 2018/6/15.
 */

public class Utils {

    public static Pattern intPattern = Pattern.compile("^[-+]?[0-9]");
    public static Pattern decimalPattern = Pattern.compile("^[-+]?[0-9]+(\\.[0-9]+)?$");

    /**
     * md5 加密
     *
     * @param str 要加密的字符串
     * @return
     */
    public static String md5Encode(String str) {
        StringBuffer buf = new StringBuffer();
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(str.getBytes());
            byte[] bytes = md5.digest();
            for (int i = 0; i < bytes.length; i++) {
                String s = Integer.toHexString(bytes[i] & 0xff);
                if (s.length() == 1) {
                    buf.append("0");
                }
                buf.append(s);
            }

        } catch (Exception ex) {
        }
        return buf.toString();
    }

    public static String getTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.RunningTaskInfo taskInfo = manager.getRunningTasks(1).get(0);
        ComponentName componentInfo = taskInfo.topActivity;
        String shortClassName = componentInfo.getShortClassName();    //类名
//        String className = info.topActivity.getClassName();              //完整类名
//        String packageName = info.topActivity.getPackageName();
        int index = shortClassName.lastIndexOf(".");
        shortClassName = shortClassName.substring(index + 1);
        return shortClassName;
    }

    //判断手机号是否正确
    public static boolean isMobileNO(String mobile) {

        String telRegex = "[1][23456789]\\d{9}";//"[1]"代表第1位为数字1，"[35678]"代表第二位可以为23456789中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。

        if (TextUtils.isEmpty(mobile)) {
            Utils.showToast("请输入正确的手机号", true);
            return false;
        } else if (!mobile.matches(telRegex)) {
            Utils.showToast("请输入正确的手机号", true);
            return false;
        }
        return true;

    }

    /**
     * 判断是否是银行卡号
     *
     * @param cardId
     * @return
     */
    public static boolean checkBankCard(String cardId) {
        char bit = getBankCardCheckCode(cardId
                .substring(0, cardId.length() - 1));
        if (bit == 'N') {
            return false;
        }
        return cardId.charAt(cardId.length() - 1) == bit;
    }

    private static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null
                || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("//d+")) {
// 如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }

    public static void showToast(String content, boolean isShort) {
        try {
            Toast toast = Toast.makeText(CustomApplication.context, content, isShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } catch (Exception e) {
            //解决在子线程中调用Toast的异常情况处理
            Looper.prepare();
            Toast.makeText(CustomApplication.context, content, isShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
            Looper.loop();
        }

    }
    public static void showToast(String content, boolean isShort,int gravity) {
        try {
            Toast toast = Toast.makeText(CustomApplication.context, content, isShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
            toast.setGravity(gravity, 0, 0);
            toast.show();
        } catch (Exception e) {
            //解决在子线程中调用Toast的异常情况处理
            Looper.prepare();
            Toast.makeText(CustomApplication.context, content, isShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
            Looper.loop();
        }

    }

    public static String ms2Date(long _ms) {
        Date date = new Date(_ms);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return format.format(date);
    }

    public static String keepDecimal2(float value) {
        NumberFormat ddf1 = NumberFormat.getNumberInstance();
        ddf1.setMaximumFractionDigits(2);
        return ddf1.format(value);
    }
    public static void copyTxt(Context context,String content) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(content);
        Toast.makeText(context, "复制成功，可以分享给朋友们了", Toast.LENGTH_LONG).show();
    }

    public static int stringToInt(String value) {
        return Integer.parseInt(value.trim());
    }

    public static double stringToDouble(String value) {
        return Double.parseDouble(value.trim());
    }

    public static float stringToFloat(String value) {
        return Float.parseFloat(value.trim());
    }

    public static float stringToDigit(String value) {


        if (intPattern.matcher(value).matches()) {
//数字
            return Integer.parseInt(value.trim());
        } else if (decimalPattern.matcher(value).matches()) {
//小数
            return Float.parseFloat(value.trim());
        } else {
//非数字
            Utils.showToast("数据格式转化出错", true);
        }
        return Float.parseFloat(value.trim());
    }
    public static  int getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    public static  String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    //假设传入的日期格式是yyyy-MM-dd HH:mm:ss, 也可以传入yyyy-MM-dd，如2018-1-1或者2018-01-01格式

    public static boolean isValidDate(String strDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2018-02-29会被接受，并转换成2018-03-01

            format.setLenient(false);
            Date date = format.parse(strDate);

            //判断传入的yyyy年-MM月-dd日 字符串是否为数字
            String[] sArray = strDate.split("-");
            for (String s : sArray) {
                boolean isNum = s.matches("[0-9]+");
                //+表示1个或多个（如"3"或"225"），*表示0个或多个（[0-9]*）（如""或"1"或"22"），?表示0个或1个([0-9]?)(如""或"7")
                if (!isNum) {
                    return false;
                }
            }
        } catch (Exception e) {
            // e.printStackTrace();
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            return false;
        }

        return true;
    }

}
