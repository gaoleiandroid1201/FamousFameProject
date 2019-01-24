package com.android.learn.base.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class CustomCrashHandler implements UncaughtExceptionHandler {
	private static final String TAG = "Activity";
	private Context mContext;
	private static final String SDCARD_ROOT = Environment.getExternalStorageDirectory().toString();
	private static CustomCrashHandler mInstance = new CustomCrashHandler();
	
	
	private CustomCrashHandler(){}

	public static CustomCrashHandler getInstance(){
		return mInstance;
	}


	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		savaInfoToSD(mContext, ex);
		
		showToast(mContext, "程序出错了，请先用其它功能，我们会尽快修复！");
		try {
			thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		android.os.Process.killProcess(android.os.Process.myPid());  
//        System.exit(0);
		
		ExitAppUtils.getInstance().exit();
		
	}


	public void init(Context context){
		mContext = context;
		Thread.setDefaultUncaughtExceptionHandler(this);
	}
	

	private void showToast(final Context context, final String msg){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
				Looper.loop();
			}
		}).start();
	}
	

	private HashMap<String, String> obtainSimpleInfo(Context context){
		HashMap<String, String> map = new HashMap<String, String>();
		PackageManager mPackageManager = context.getPackageManager();
		PackageInfo mPackageInfo = null;
		try {
			mPackageInfo = mPackageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		map.put("versionName", mPackageInfo.versionName);
		map.put("versionCode", "" + mPackageInfo.versionCode);
		
		map.put("MODEL", "" + Build.MODEL);
		map.put("SDK_INT", "" + Build.VERSION.SDK_INT);
		map.put("PRODUCT", "" +  Build.PRODUCT);
		
		return map;
	}
	

	private String obtainExceptionInfo(Throwable throwable) {
		StringWriter mStringWriter = new StringWriter();
		PrintWriter mPrintWriter = new PrintWriter(mStringWriter);
		throwable.printStackTrace(mPrintWriter);
		mPrintWriter.close();
		
		Log.e(TAG, mStringWriter.toString());
		return mStringWriter.toString();
	}
	

	private String savaInfoToSD(Context context, Throwable ex){
		String fileName = null;
		StringBuffer sb = new StringBuffer();
		
		for (Map.Entry<String, String> entry : obtainSimpleInfo(context).entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key).append(" = ").append(value).append("\n");
		}  
		
		sb.append(obtainExceptionInfo(ex));
		
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			File dir = new File(SDCARD_ROOT + File.separator + "crash" + File.separator);
			if(! dir.exists()){
				dir.mkdir();
			}
			
			try{
				fileName = dir.toString() + File.separator + paserTime(System.currentTimeMillis()) + ".log";
				FileOutputStream fos = new FileOutputStream(fileName);
				fos.write(sb.toString().getBytes());
				fos.flush();
				fos.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
		return fileName;
		
	}
	
	
	private String paserTime(long milliseconds) {
		System.setProperty("user.timezone", "Asia/Shanghai");
		TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
		TimeZone.setDefault(tz);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String times = format.format(new Date(milliseconds));
		
		return times;
	}
}
