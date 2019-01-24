package com.android.learn.base.utils;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

public class
PermissionUtil {

    private static String denyRequestContent = "%s权限 为必要权限，开通才可以正常使用相应功能";
    private static String foreverDenyRequestContent = "%s权限 为必要权限,开通才可以正常使用相应功能。\n \n 请点击 \"设置\"-\"权限\"-打开所需权限。";
    public static final int PERMISSION_CODE = 10001;

    public static void gotoDetailSettingIntent(Activity context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(intent);

//        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
//        intent.setData(uri);
    }

    public static void requestDenyDialog(final Activity activity, final String... permissions) {

        String content = getContent(denyRequestContent, permissions);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle("权限申请")
                .setMessage(content)
                .setPositiveButton("申请", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ActivityCompat.requestPermissions(activity, permissions, PERMISSION_CODE);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Utils.getTopActivity(activity).equals("MainActivity")) {
                            activity.finish();
                        }
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    public static void requestForeverDenyDialog(final Activity activity, final String... permissions) {

        String content = getContent(foreverDenyRequestContent, permissions);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle("权限设置")
                .setMessage(content)
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        gotoDetailSettingIntent(activity);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    public static String getContent(String ifForeverDeny, String... permissions) {
        String content = "";

        if (permissions.length == 1) {
            if (permissions[0].equals(Manifest.permission.CAMERA))
                content = String.format(ifForeverDeny, "相机");
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                content = String.format(ifForeverDeny, "存储");
        }
        if (permissions.length > 1) {
            StringBuilder stringBuilder = new StringBuilder();
            int length = permissions.length;
            for (int i = 0; i < length; i++) {
                if (permissions[i].equals(Manifest.permission.CAMERA))
                    stringBuilder = stringBuilder.append("相机");
                if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    stringBuilder = stringBuilder.append("存储");
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION))
                    stringBuilder = stringBuilder.append("位置");
                if (permissions[i].equals(Manifest.permission.WRITE_CONTACTS))
                    stringBuilder = stringBuilder.append("联系人");
                if (i < length - 1)
                    stringBuilder = stringBuilder.append("、");
            }
            content = String.format(ifForeverDeny, stringBuilder.toString());
        }
        return content;

    }

    /**
     * 权限请求结果回调接口
     */
    public interface RequestPermissionCallBack {
        /**
         * 同意授权
         */
        public void granted();

        /**
         * 取消授权
         */
        public void denied();
    }
}
