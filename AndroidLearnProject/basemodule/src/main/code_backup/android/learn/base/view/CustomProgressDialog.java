package com.android.learn.base.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gaolei.basemodule.R;


public class CustomProgressDialog {
    public static Dialog loadingDialog;

    public static Dialog createLoadingDialog(Context context) {
        if (context == null) return null;
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_loading, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.anim_dialog_load);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);

        loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

        loadingDialog.setCancelable(true);// 不可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        return loadingDialog;

    }

    public static void show(Activity activity) {
        if (loadingDialog != null) return;
        if (loadingDialog != null && loadingDialog.isShowing()) return;
        loadingDialog = CustomProgressDialog.createLoadingDialog(activity);
        loadingDialog.show();//显示
    }

    public static void cancel() {
        if (loadingDialog != null) {
            loadingDialog.cancel();
            loadingDialog = null;
        }
    }
}

