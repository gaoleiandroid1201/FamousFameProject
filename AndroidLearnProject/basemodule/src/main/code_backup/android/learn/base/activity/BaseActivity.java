package com.android.learn.base.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.view.View;

import com.android.learn.base.application.CustomApplication;
import com.android.learn.base.utils.ExitAppUtils;
import com.android.learn.base.utils.LogUtil;
import com.android.learn.base.utils.SPUtils;
import com.gaolei.basemodule.R;
import com.jaeger.library.StatusBarUtil;
import com.umeng.analytics.MobclickAgent;
import com.wind.me.xskinloader.SkinInflaterFactory;
import com.wind.me.xskinloader.SkinManager;
import com.wind.me.xskinloader.util.AssetFileUtils;

import java.io.File;

import butterknife.ButterKnife;
import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;


/**
 * Created by gaolei on 2018/4/26.
 */

public abstract class BaseActivity extends BasePermisssionActivity implements View.OnClickListener, BGASwipeBackHelper.Delegate {

    Boolean isNightMode;

    String TAG = "BaseActivity";
    protected BGASwipeBackHelper mSwipeBackHelper;

    protected void onCreate(@Nullable Bundle savedInstanceState) {

// 「必须在 Application 的 onCreate 方法中执行 BGASwipeBackHelper.init 来初始化滑动返回」
        // 在 super.onCreate(savedInstanceState) 之前调用该方法
        initSwipeBackFinish();
        super.onCreate(savedInstanceState);
        SkinInflaterFactory.setFactory(this);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        setStatusBar();
        ExitAppUtils.getInstance().addActivity(this);

        SkinManager.get().setWindowStatusBarColor(this.getWindow(), R.color.status_bar_color);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            bundle = savedInstanceState;
        }
        initData(bundle);

    }


    protected abstract int getLayoutId();

    protected abstract void initData(Bundle bundle);

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            finish();
        }
    }

    protected void onRestart() {
        super.onRestart();
    }

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
//        context = this;
        LogUtil.d(TAG, "BaseActivity ----onResume：" + getClass().getName().toString());
    }


    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    protected void onDestroy() {
        super.onDestroy();
        ExitAppUtils.getInstance().delActivity(this);
        LogUtil.d(TAG, "BaseActivity ----onDestroy：" + getClass().getName().toString());
    }

    public void useNightMode(boolean isNight) {

        if (isNight) {
            changeSkin();

        } else {

            restoreDefaultSkin();
        }
    }

    //重写字体缩放比例  api>25
    @Override
    protected void attachBaseContext(Context newBase) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            final Resources res = newBase.getResources();
            final Configuration config = res.getConfiguration();
            config.fontScale = CustomApplication.getInstance().getFontScale();//1 设置正常字体大小的倍数
            final Context newContext = newBase.createConfigurationContext(config);
            super.attachBaseContext(newContext);
        } else {
            super.attachBaseContext(newBase);
        }
    }

    //重写字体缩放比例 api<25
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            Configuration config = res.getConfiguration();
            config.fontScale = CustomApplication.getInstance().getFontScale();//1 设置正常字体大小的倍数
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        return res;
    }

    private void changeSkin() {
        //将assets目录下的皮肤文件拷贝到data/data/.../cache目录下
        String saveDir = getCacheDir().getAbsolutePath() + "/skins";
        String savefileName = "/skin1.skin";
        String asset_dir = "skins/xskinloader-skin-apk-debug.apk";
        File file = new File(saveDir + File.separator + savefileName);
//        if (!file.exists()) {
        AssetFileUtils.copyAssetFile(this, asset_dir, saveDir, savefileName);
//        }
        SkinManager.get().loadNewSkin(file.getAbsolutePath());
    }

    private void restoreDefaultSkin() {
        SkinManager.get().restoreToDefaultSkin();
    }


    /**
     * 初始化滑动返回。在 super.onCreate(savedInstanceState) 之前调用该方法
     */
    private void initSwipeBackFinish() {
        mSwipeBackHelper = new BGASwipeBackHelper(this, this);

        // 「必须在 Application 的 onCreate 方法中执行 BGASwipeBackHelper.init 来初始化滑动返回」
        // 下面几项可以不配置，这里只是为了讲述接口用法。

        // 设置滑动返回是否可用。默认值为 true
        mSwipeBackHelper.setSwipeBackEnable(true);
        // 设置是否仅仅跟踪左侧边缘的滑动返回。默认值为 true
        mSwipeBackHelper.setIsOnlyTrackingLeftEdge(false);
        // 设置是否是微信滑动返回样式。默认值为 true
        mSwipeBackHelper.setIsWeChatStyle(true);
        // 设置阴影资源 id。默认值为 R.drawable.bga_sbl_shadow
        mSwipeBackHelper.setShadowResId(R.drawable.bga_sbl_shadow);
        // 设置是否显示滑动返回的阴影效果。默认值为 true
        mSwipeBackHelper.setIsNeedShowShadow(true);
        // 设置阴影区域的透明度是否根据滑动的距离渐变。默认值为 true
        mSwipeBackHelper.setIsShadowAlphaGradient(true);
        // 设置触发释放后自动滑动返回的阈值，默认值为 0.3f
        mSwipeBackHelper.setSwipeBackThreshold(0.3f);
        // 设置底部导航条是否悬浮在内容上，默认值为 false
        mSwipeBackHelper.setIsNavigationBarOverlap(false);
    }

    /**
     * 是否支持滑动返回。这里在父类中默认返回 true 来支持滑动返回，如果某个界面不想支持滑动返回则重写该方法返回 false 即可
     *
     * @return
     */
    @Override
    public boolean isSupportSwipeBack() {
        return true;
    }

    /**
     * 正在滑动返回
     *
     * @param slideOffset 从 0 到 1
     */
    @Override
    public void onSwipeBackLayoutSlide(float slideOffset) {
    }

    /**
     * 没达到滑动返回的阈值，取消滑动返回动作，回到默认状态
     */
    @Override
    public void onSwipeBackLayoutCancel() {
    }

    /**
     * 滑动返回执行完毕，销毁当前 Activity
     */
    @Override
    public void onSwipeBackLayoutExecuted() {
        mSwipeBackHelper.swipeBackward();
    }

    @Override
    public void onBackPressed() {
        // 正在滑动返回的时候取消返回按钮事件
        if (mSwipeBackHelper.isSliding()) {
            return;
        }
        mSwipeBackHelper.backward();
    }

    protected void setStatusBar() {
        Boolean isNightMode = (Boolean) SPUtils.getParam(this, "nightMode", new Boolean(false));
        if (isNightMode) {
            StatusBarUtil.setColorForSwipeBack(this, getResources().getColor(R.color.app_color_night), 0);
        } else
            StatusBarUtil.setColorForSwipeBack(this, getResources().getColor(R.color.app_color), 0);
    }
}