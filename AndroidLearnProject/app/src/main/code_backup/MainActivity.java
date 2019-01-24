package com.android.learn;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.learn.activity.SearchResultActivity;
import com.android.learn.adapter.MainTabAdapter;
import com.android.learn.adapter.SearchRecordAdapter;
import com.android.learn.base.activity.BaseMvpActivity;
import com.android.learn.base.db.DBManager;
import com.android.learn.base.db.SearchRecord;
import com.android.learn.base.event.ChangeNightEvent;
import com.android.learn.base.event.RestartMainEvent;
import com.android.learn.base.mmodel.HotKeyData;
import com.android.learn.base.utils.KeyboardUtils;
import com.android.learn.base.utils.LogUtil;
import com.android.learn.base.utils.PermissionUtil;
import com.android.learn.base.utils.SPUtils;
import com.android.learn.base.utils.ScreenUtils;
import com.android.learn.base.utils.Utils;
import com.android.learn.base.view.TitleView;
import com.android.learn.fragment.HomeFragment;
import com.android.learn.fragment.KnowledgeFragment;
import com.android.learn.fragment.ProjectFragment;
import com.android.learn.fragment.UserFragment;
import com.android.learn.fragment.WechatFragment;
import com.android.learn.mcontract.MainActivityContract;
import com.android.learn.mpresenter.MainActivityPresenter;
import com.android.learn.view.CustomViewPager;
import com.android.learn.view.SearchViewUtils;
import com.android.speechdemo.xf.JsonParser;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.sunflower.FlowerCollector;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGADynamicEntity;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.lankton.flowlayout.FlowLayout;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends BaseMvpActivity<MainActivityPresenter> implements MainActivityContract.View {

    private ArrayList<Fragment> mFragments;
    private ArrayList<String> titles;

    @BindView(R.id.viewPager)
    CustomViewPager viewPager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.header_layout)
    TitleView header_layout;
    @BindView(R.id.iv_svga)
    SVGAImageView iv_svga;
    @BindView(R.id.cardview_search)
    LinearLayout cardview_search;
    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.iv_search)
    ImageView iv_search;
    @BindView(R.id.iv_search_back)
    ImageView iv_search_back;
    @BindView(R.id.history_recycleview)
    RecyclerView history_recycleview;
    @BindView(R.id.title_view_divider)
    View title_view_divider;
    @BindView(R.id.flowlayout)
    FlowLayout flowlayout;
    HomeFragment homeFragment;
    ProjectFragment projectFragment;
    boolean isSearching;
    SearchRecordAdapter searchRecordAdapter;
    String TAG = "MainActivity";
    private SpeechRecognizer mIat;
    private RecognizerDialog mIatDialog;
    private Toast mToast;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    // 引擎类型 这里我只考虑了一种引擎类型 :云端的
    private String mCLOUDType = SpeechConstant.TYPE_CLOUD;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData(Bundle bundle) {
        initView();
        requestPermission();
        EventBus.getDefault().register(this);
    }

    protected void initView() {
        mFragments = new ArrayList<Fragment>();
        homeFragment = new HomeFragment();
        projectFragment = new ProjectFragment();
        mFragments.add(homeFragment);
        mFragments.add(projectFragment);
        mFragments.add(new KnowledgeFragment());
        mFragments.add(new WechatFragment());
        mFragments.add(new UserFragment());

        titles = new ArrayList<String>();
        titles.add(getString(R.string.home));
        titles.add(getString(R.string.project));
        titles.add(getString(R.string.knowledge));
        titles.add(getString(R.string.public_account));
        titles.add(getString(R.string.mine));

        MainTabAdapter adapter = new MainTabAdapter(getSupportFragmentManager(), mFragments);
        viewPager.setOffscreenPageLimit(mFragments.size());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        //将TabLayout和ViewPager关联起来
        tabLayout.setupWithViewPager(viewPager);
        initTab();
        initSearchRecord();
        iv_search.setVisibility(View.VISIBLE);
        Boolean isNightMode = (Boolean) SPUtils.getParam(this, "nightMode", new Boolean(false));
        if (isNightMode) {
            title_view_divider.setVisibility(View.VISIBLE);
        } else {
            title_view_divider.setVisibility(View.GONE);
        }

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {//搜索按键action
                    beginSearch();
                }
                return false;
            }
        });

    }

    private void beginSearch() {
        String content = et_search.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Utils.showToast(getString(R.string.search_content_no), true);
        }

        Bundle bundle = new Bundle();
        bundle.putString("key", content);
        SearchResultActivity.startActivity(MainActivity.this, bundle);
        DBManager dbManager = DBManager.getInstance(MainActivity.this);
        SearchRecord searchRecord = new SearchRecord();
        searchRecord.setName(content);
        dbManager.insertUser(searchRecord);
        et_search.setText("");
        KeyboardUtils.hideKeyboard(et_search);
    }

    private void initSearchRecord() {
        ArrayList<SearchRecord> list = new ArrayList<>();
        searchRecordAdapter = new SearchRecordAdapter(this, list);
        history_recycleview.setLayoutManager(new LinearLayoutManager(this));
        history_recycleview.setAdapter(searchRecordAdapter);
        searchRecordAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putString("key", searchRecordAdapter.getData().get(position).getName());
                SearchResultActivity.startActivity(MainActivity.this, bundle);
            }
        });
    }

    /**
     * 设置添加Tab
     */
    private void initTab() {

        tabLayout.getTabAt(0).setCustomView(R.layout.tab_home);
        tabLayout.getTabAt(1).setCustomView(R.layout.tab_project);
        tabLayout.getTabAt(2).setCustomView(R.layout.tab_knowledge);
        tabLayout.getTabAt(3).setCustomView(R.layout.tab_navigation);
        tabLayout.getTabAt(4).setCustomView(R.layout.tab_mine);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            //标签选中之后执行的方法
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                title.setText(titles.get(position));

                if (position == 4) header_layout.setVisibility(View.GONE);
                else header_layout.setVisibility(View.VISIBLE);
            }

            //标签没选中
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        Boolean isRestartMain = (Boolean) SPUtils.getParam(this, "isRestartMain", new Boolean(false));
        LogUtil.d(TAG, "isRestartMain：" + isRestartMain);
        if (isRestartMain) {
            //切换语言或切换字体大小，重启MainActivity则会走这里
            viewPager.setCurrentItem(4);
//            tabLayout.getTabAt(4).getCustomView().setSelected(true);
            SPUtils.setParam(MainActivity.this, "isRestartMain", new Boolean(false));
        } else {
            //默认选中的Tab
            viewPager.setCurrentItem(0);
//            tabLayout.getTabAt(0).getCustomView().setSelected(true);
            loadSVGAAnimation();

        }

    }

    @OnClick({R.id.title, R.id.iv_svga, R.id.iv_search_back, R.id.iv_search, R.id.tv_search_clear, R.id.iv_speech_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title:
                int index = viewPager.getCurrentItem();
                if (index == 0)
                    homeFragment.scrollToTop();
                if (index == 1)
                    projectFragment.scrollToTop();
                break;

            case R.id.iv_svga:
                iv_svga.stopAnimation();
                iv_svga.setVisibility(View.GONE);
                break;
            case R.id.iv_search_back:
                SearchViewUtils.handleToolBar(getApplicationContext(), cardview_search, et_search);
                break;
            case R.id.iv_search:
                SearchViewUtils.handleToolBar(getApplicationContext(), cardview_search, et_search);
                isSearching = true;
                break;
            case R.id.tv_search_clear:
                searchRecordAdapter.getData().clear();
                searchRecordAdapter.notifyDataSetChanged();
                DBManager.getInstance(this).deleteAll();
                break;
            case R.id.iv_speech_search:
                KeyboardUtils.hideKeyboard(et_search);
                requestRecordAudioPermission();
                break;
        }

    }

    public void requestPermission() {
        requestPermission(this, new PermissionUtil.RequestPermissionCallBack() {

            @Override
            public void granted() {

            }

            @Override
            public void denied() {
            }
        }, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE});
    }

    public void onRestart() {
        super.onRestart();
        //跳转到设置界面后返回，重新检查权限
        requestPermission();
    }

    public void onStart() {
        super.onStart();
        List<SearchRecord> recordList = DBManager.getInstance(this).queryUserList();
        searchRecordAdapter.getData().clear();
        searchRecordAdapter.addData(recordList);

    }

    // 用来计算返回键的点击间隔时间
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (isSearching) {
                SearchViewUtils.handleToolBar(getApplicationContext(), cardview_search, et_search);
                isSearching = false;
                return false;
            }
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                //弹出提示，可以有多种方式
                Toast.makeText(getApplicationContext(), getString(R.string.press_exit), Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    private void loadSVGAAnimation() {
        SVGAParser parser = new SVGAParser(this);
        resetDownloader(parser);
        try {
            parser.parse(new URL("https://github.com/yyued/SVGA-Samples/blob/master/kingset.svga?raw=true"), new SVGAParser.ParseCompletion() {
                @Override
                public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                    SVGADrawable drawable = new SVGADrawable(videoItem, requestDynamicItemWithSpannableText());
                    iv_svga.setImageDrawable(drawable);
                    iv_svga.startAnimation();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            /**
                             *要执行的操作
                             */
                            iv_svga.stopAnimation();
                            iv_svga.setVisibility(View.GONE);
                        }
                    }, 5000);

                }

                @Override
                public void onError() {

                }
            });
        } catch (Exception e) {
            System.out.print(true);
        }
    }

    /**
     * 你可以设置富文本到 ImageKey 相关的元素上
     * 富文本是会自动换行的，不要设置过长的文本
     *
     * @return
     */
    private SVGADynamicEntity requestDynamicItemWithSpannableText() {
        SVGADynamicEntity dynamicEntity = new SVGADynamicEntity();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(getString(R.string.welcome_learn_android));
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(28);
        dynamicEntity.setDynamicText(new StaticLayout(
                spannableStringBuilder,
                0,
                spannableStringBuilder.length(),
                textPaint,
                0,
                android.text.Layout.Alignment.ALIGN_CENTER,
                1.0f,
                0.0f,
                false
        ), "banner");
        dynamicEntity.setDynamicDrawer(new Function2<Canvas, Integer, Boolean>() {
            @Override
            public Boolean invoke(Canvas canvas, Integer frameIndex) {
                Paint aPaint = new Paint();
                aPaint.setColor(Color.WHITE);
                canvas.drawCircle(50, 54, frameIndex % 5, aPaint);
                return false;
            }
        }, "banner");
        return dynamicEntity;
    }

    /**
     * 设置下载器，这是一个可选的配置项。
     *
     * @param parser
     */
    private void resetDownloader(SVGAParser parser) {
        parser.setFileDownloader(new SVGAParser.FileDownloader() {
            @Override
            public void resume(final URL url, final Function1<? super InputStream, Unit> complete, final Function1<? super Exception, Unit> failure) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder().url(url).get().build();
                        try {
                            Response response = client.newCall(request).execute();
                            complete.invoke(response.body().byteStream());
                        } catch (IOException e) {
                            e.printStackTrace();
                            failure.invoke(e);
                        }
                    }
                }).start();
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RestartMainEvent event) {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        event.activity.finish();
        SPUtils.setParam(MainActivity.this, "isRestartMain", new Boolean(true));

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ChangeNightEvent event) {
        Boolean isNightMode = (Boolean) SPUtils.getParam(this, "nightMode", new Boolean(false));
        if (isNightMode) {
            title_view_divider.setVisibility(View.VISIBLE);
        } else {
            title_view_divider.setVisibility(View.GONE);
        }
        setStatusBar();
    }

    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public MainActivityPresenter initPresenter() {
        return new MainActivityPresenter();
    }

    @Override
    protected void loadData() {
        mPresenter.getHotKey();
    }


    @Override
    public void showHotKey(List<HotKeyData> list) {
        addFolowLayoutView(list);

    }

    private void addFolowLayoutView(List<HotKeyData> list) {
        int length = list.size();
        for (int i = 0; i < list.size(); i++) {
            final HotKeyData hotKeyData = list.get(i);
            int ranHeight = ScreenUtils.dp2px(this, 30);
            ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ranHeight);
            lp.setMargins(ScreenUtils.dp2px(this, 10), 0, ScreenUtils.dp2px(this, 10), 0);
            final TextView tv = new TextView(this);
            tv.setPadding(ScreenUtils.dp2px(this, 15), 0, ScreenUtils.dp2px(this, 15), 0);
            tv.setTextColor(Color.parseColor("#FF3030"));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            int index = (int) (Math.random() * length);
            tv.setText(hotKeyData.getName());
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setLines(1);
            tv.setBackgroundResource(R.drawable.bg_tag);
            flowlayout.addView(tv, lp);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("key", tv.getText().toString());
                    SearchResultActivity.startActivity(MainActivity.this, bundle);
                    DBManager dbManager = DBManager.getInstance(MainActivity.this);
                    SearchRecord searchRecord = new SearchRecord();
                    searchRecord.setName(tv.getText().toString());
                    dbManager.insertUser(searchRecord);
                }
            });
        }
        flowlayout.relayoutToAlign();
    }

    public void requestRecordAudioPermission() {
        requestPermission(this, new PermissionUtil.RequestPermissionCallBack() {
            @Override
            public void granted() {
                // 使用SpeechRecognizer对象，可根据回调消息自定义界面；这种方式主要是考虑到了，没有听写Dialog的时候，进行的听写监听
                mIat = SpeechRecognizer.createRecognizer(MainActivity.this, mInitListener);
                Log.i(TAG, "onCreate: mIat == null ?" + mIat);
                //SpeechRecognizer对象 null 的原因：一、 so 文件放错了位置 二、so文件与自己的SDK不匹配 3、Application中没有配置好appid
                // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
                // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置显示RecognizerDialog需要的布局文件和图片资源
                mIatDialog = new RecognizerDialog(MainActivity.this, mInitListener);
                // 移动数据分析，收集开始听写事件
                FlowerCollector.onEvent(MainActivity.this, "iat_recognize");

                et_search.setText("");// 清空显示内容
                mIatResults.clear();
                // 设置参数
                setParam();

                // 显示听写对话框
                mIatDialog.setListener(mRecognizerDialogListener);
                mIatDialog.show();
                Utils.showToast(getString(R.string.begin_speech), true, Gravity.BOTTOM);
            }

            @Override
            public void denied() {
            }
        }, new String[]{Manifest.permission.RECORD_AUDIO});
    }

    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎  注意：这里我只设置云端的方式！后面再考虑本地和混合的类型
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mCLOUDType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        // 设置语言
        mIat.setParameter(SpeechConstant.LANGUAGE, "cn");
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "0");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
    }

    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
        }
    };
    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        /**
         * 识别成功时回调数据
         */
        public void onResult(RecognizerResult results, boolean isLast) {
            LogUtil.d(TAG, "printResult(results)------------");
            printResult(results);
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {

        }
    };

    /**
     * 成功时显示说话的文字
     *
     * @param results
     */
    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

        et_search.setText(resultBuffer.toString());
        //考虑到TextView只能显示文字 ，后面还要测试文字转语音，所以换EditText控件
        et_search.setSelection(et_search.length());
        KeyboardUtils.showKeyboard(et_search);
        LogUtil.d(TAG, "printResult(results)------------resultBuffer.toString():" + resultBuffer.toString());
        if (et_search.getText().toString().length() > 0 && !"SearchResultActivity".equals(Utils.getTopActivity(this)))
            beginSearch();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 在这里添加屏幕切换后的操作
    }
}
