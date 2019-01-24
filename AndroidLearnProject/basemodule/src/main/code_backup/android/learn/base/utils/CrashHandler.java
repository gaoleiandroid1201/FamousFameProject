package com.android.learn.base.utils;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;


import com.android.learn.base.application.CustomApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    /**
     * 系统默认的异常处理类
     */
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    Context mcontext;
    private static CrashHandler INSTANCE = new CrashHandler();
    String errorSavePath;
    //用来存储设备信息和异常信息
    private Map<String, String> infos = new LinkedHashMap();

    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    public void init(Context context) {
        mcontext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();// 获取默认的异常处理类
        Thread.setDefaultUncaughtExceptionHandler(this);// 设置当前处理类为默认的异常处理类
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        saveCrashInfoIntoSd(ex);
        showToast(mcontext, "程序出错了，请先用其它功能，我们会尽快修复！");

        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            // TODO: handle exception
        }


//            if (SPUtils.contains("currentTime")) {
//                String lastTimeMillis = (String) SPUtils.get("currentTime", String.valueOf("0"));
//                LogUtil.d("lastTimeMillis:" + lastTimeMillis);
//                LogUtil.d("System.currentTimeMillis():" + System.currentTimeMillis());
//                LogUtil.d("priod:" + (System.currentTimeMillis() - Long.valueOf(lastTimeMillis)));
//                long timeInterval = System.currentTimeMillis() - Long.valueOf(lastTimeMillis);
//                if (timeInterval > 60 * 1000) {
//                    SPUtils.put("currentTime", String.valueOf(System.currentTimeMillis()));
//                }
//            } else {
//                SPUtils.put("currentTime", String.valueOf(System.currentTimeMillis()));
//
//            }

//            android.os.Process.killProcess(android.os.Process.myPid());
//            System.exit(0);
        ExitAppUtils.getInstance().exit();
//        }
    }

    private void showToast(final Context context, final String msg) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }).start();
    }


    // 收集设备、软件参数信息
    private void collectDeviceInfo() {
        try {
            PackageManager pm = CustomApplication.context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(CustomApplication.context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("systemVersion", SystemUtil.getSystemVersion());
                infos.put("deviceModel", SystemUtil.getSystemModel());
                infos.put("deviceBrand", SystemUtil.getDeviceBrand());
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
        }

    }

    // 保存错误信息到SD卡文件中
    private void saveCrashInfoIntoSd(Throwable ex) {
        collectDeviceInfo();
        //创建文件夹
        errorSavePath = Environment.getExternalStorageDirectory().getPath() + "/" + mcontext.getPackageName() + "/crash";
        File dir = new File(errorSavePath);
        if (!dir.exists()) dir.mkdirs();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = formatter.format(new Date());
        StringBuffer sb = new StringBuffer();
        sb.append("\n" + time + "\n");
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        sb.append(getCrashInfo(ex));

        try {
            //创建文件
            String fileName = "crash-" + time + ".txt";
            File file = new File(errorSavePath + "//" + fileName);
            if (!file.exists()) file.createNewFile();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(sb.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到程序崩溃的详细信息
     */
    public String getCrashInfo(Throwable ex) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        ex.setStackTrace(ex.getStackTrace());
        ex.printStackTrace(printWriter);
        printWriter.close();
        return result.toString();
    }


}