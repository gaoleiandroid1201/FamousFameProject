package com.android.learn

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.android.learn.activity.LanguageActivity
import com.android.learn.activity.SearchResultActivity
import com.android.learn.adapter.MainTabAdapter
import com.android.learn.adapter.SearchRecordAdapter
import com.android.learn.base.activity.BaseActivity
import com.android.learn.base.activity.BaseMvpActivity
import com.android.learn.base.db.DBManager
import com.android.learn.base.db.SearchRecord
import com.android.learn.base.event.ChangeNightEvent
import com.android.learn.base.event.RestartMainEvent
import com.android.learn.base.mmodel.HotKeyData
import com.android.learn.base.utils.KeyboardUtils
import com.android.learn.base.utils.LanguageUtil
import com.android.learn.base.utils.LogUtil
import com.android.learn.base.utils.PermissionUtil
import com.android.learn.base.utils.SPUtils
import com.android.learn.base.utils.ScreenUtils
import com.android.learn.base.utils.Utils
import com.android.learn.base.view.TitleView
import com.android.learn.fragment.HomeFragment
import com.android.learn.fragment.KnowledgeFragment
import com.android.learn.fragment.WechatFragment
import com.android.learn.fragment.ProjectFragment
import com.android.learn.fragment.UserFragment
import com.android.learn.mcontract.MainActivityContract
import com.android.learn.mpresenter.MainActivityPresenter
import com.android.learn.view.CustomViewPager
import com.android.learn.view.SearchViewUtils
import com.android.speechdemo.bean.TalkBackVo
import com.android.speechdemo.xf.JsonParser
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.iflytek.cloud.ErrorCode
import com.iflytek.cloud.InitListener
import com.iflytek.cloud.RecognizerResult
import com.iflytek.cloud.SpeechConstant
import com.iflytek.cloud.SpeechError
import com.iflytek.cloud.SpeechRecognizer
import com.iflytek.cloud.SpeechUtility
import com.iflytek.cloud.ui.RecognizerDialog
import com.iflytek.cloud.ui.RecognizerDialogListener
import com.iflytek.sunflower.FlowerCollector
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGADynamicEntity
import com.opensource.svgaplayer.SVGAImageView
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject

import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Type
import java.net.URL
import java.util.ArrayList
import java.util.HashMap
import java.util.LinkedHashMap

import butterknife.BindView
import butterknife.OnClick
import cn.lankton.flowlayout.FlowLayout
import com.android.learn.base.mpresenter.BasePresenter
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class MainActivity : BaseMvpActivity<MainActivityPresenter>(), MainActivityContract.View {

    private lateinit var mFragments: ArrayList<Fragment>
    private lateinit var titles: ArrayList<String>

    @BindView(R.id.viewPager)
    lateinit var viewPager: CustomViewPager
    @BindView(R.id.tabLayout)
    lateinit var tabLayout: TabLayout
    @BindView(R.id.title)
    lateinit var title: TextView
    @BindView(R.id.header_layout)
    lateinit var header_layout: TitleView
    @BindView(R.id.iv_svga)
    lateinit var iv_svga: SVGAImageView
    @BindView(R.id.cardview_search)
    lateinit var cardview_search: LinearLayout
    @BindView(R.id.et_search)
    lateinit var et_search: EditText
    @BindView(R.id.iv_search)
    lateinit var iv_search: ImageView
    @BindView(R.id.iv_search_back)
    lateinit var iv_search_back: ImageView
    @BindView(R.id.history_recycleview)
    lateinit var history_recycleview: RecyclerView
    @BindView(R.id.title_view_divider)
    lateinit var title_view_divider: View
    @BindView(R.id.flowlayout)
    lateinit var flowlayout: FlowLayout
    lateinit var homeFragment: HomeFragment
    lateinit var projectFragment: ProjectFragment
    var isSearching: Boolean = false
    lateinit var searchRecordAdapter: SearchRecordAdapter
    var TAG = "MainActivity"
    private lateinit var mIat: SpeechRecognizer
    private lateinit var mIatDialog: RecognizerDialog
    // 用HashMap存储听写结果
    private val mIatResults = LinkedHashMap<String?, String>()
    // 引擎类型 这里我只考虑了一种引擎类型 :云端的
    private val mCLOUDType = SpeechConstant.TYPE_CLOUD

    // 用来计算返回键的点击间隔时间
    private var exitTime: Long = 0

    private val mInitListener = InitListener { }

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }

    /**
     * 听写UI监听器
     */
    private val mRecognizerDialogListener = object : RecognizerDialogListener {
        /**
         * 识别成功时回调数据
         */
        override fun onResult(results: RecognizerResult, isLast: Boolean) {
            LogUtil.d(TAG, "printResult(results)------------")
            printResult(results)
        }

        /**
         * 识别回调错误.
         */
        override fun onError(error: SpeechError) {

        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initData(bundle: Bundle?) {
        initView()
        requestPermission()
        EventBus.getDefault().register(this)
    }

    protected fun initView() {
        mFragments = ArrayList()
        homeFragment = HomeFragment()
        projectFragment = ProjectFragment()
        mFragments.add(homeFragment)
        mFragments.add(projectFragment)
        mFragments.add(KnowledgeFragment())
        mFragments.add(WechatFragment())
        mFragments.add(UserFragment())

        titles = ArrayList()
        titles.add(getString(R.string.home))
        titles.add(getString(R.string.project))
        titles.add(getString(R.string.knowledge))
        titles.add(getString(R.string.public_account))
        titles.add(getString(R.string.mine))

        val adapter = MainTabAdapter(supportFragmentManager, mFragments)
        viewPager.offscreenPageLimit = mFragments.size
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewPager))
        //将TabLayout和ViewPager关联起来
        tabLayout.setupWithViewPager(viewPager)
        initTab()
        initSearchRecord()
        iv_search.visibility = View.VISIBLE
        val isNightMode = SPUtils.getParam(this, "nightMode", false) as Boolean
        if (isNightMode) {
            title_view_divider.visibility = View.VISIBLE
        } else {
            title_view_divider.visibility = View.GONE
        }

        et_search.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {//搜索按键action
                beginSearch()
            }
            false
        }

    }

    private fun beginSearch() {
        val content = et_search.text.toString()
        if (TextUtils.isEmpty(content)) {
            Utils.showToast(getString(R.string.search_content_no), true)
        }

        val bundle = Bundle()
        bundle.putString("key", content)
        SearchResultActivity.startActivity(this@MainActivity, bundle)
        val dbManager = DBManager.getInstance(this@MainActivity)
        val searchRecord = SearchRecord()
        searchRecord.name = content
        dbManager.insertUser(searchRecord)
        et_search.setText("")
        KeyboardUtils.hideKeyboard(et_search)
    }

    private fun initSearchRecord() {
        val list = ArrayList<SearchRecord>()
        searchRecordAdapter = SearchRecordAdapter(this, list)
        history_recycleview.layoutManager = LinearLayoutManager(this)
        history_recycleview.adapter = searchRecordAdapter
        searchRecordAdapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
            val bundle = Bundle()
            bundle.putString("key", searchRecordAdapter.data[position].name)
            SearchResultActivity.startActivity(this@MainActivity, bundle)
        }
    }

    /**
     * 设置添加Tab
     */
    private fun initTab() {

        tabLayout.getTabAt(0)!!.setCustomView(R.layout.tab_home)
        tabLayout.getTabAt(1)!!.setCustomView(R.layout.tab_project)
        tabLayout.getTabAt(2)!!.setCustomView(R.layout.tab_knowledge)
        tabLayout.getTabAt(3)!!.setCustomView(R.layout.tab_navigation)
        tabLayout.getTabAt(4)!!.setCustomView(R.layout.tab_mine)


        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            //标签选中之后执行的方法
            override fun onTabSelected(tab: TabLayout.Tab) {
                val position = tab.position
                title.text = titles[position]

                if (position == 4)
                    header_layout.visibility = View.GONE
                else
                    header_layout.visibility = View.VISIBLE
            }

            //标签没选中
            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
        val isRestartMain = SPUtils.getParam(this, "isRestartMain", false) as Boolean
        LogUtil.d(TAG, "isRestartMain：$isRestartMain")
        if (isRestartMain) {
            //切换语言或切换字体大小，重启MainActivity则会走这里
            viewPager.currentItem = 4
            //            tabLayout.getTabAt(4).getCustomView().setSelected(true);
            SPUtils.setParam(this@MainActivity, "isRestartMain", false)
        } else {
            //默认选中的Tab
            viewPager.currentItem = 0
            //            tabLayout.getTabAt(0).getCustomView().setSelected(true);
            loadSVGAAnimation()

        }

    }

    @OnClick(R.id.title, R.id.iv_svga, R.id.iv_search_back, R.id.iv_search, R.id.tv_search_clear, R.id.iv_speech_search)
    override fun onClick(view: View) {
        when (view.id) {
            R.id.title -> {
                val index = viewPager.currentItem
                if (index == 0)
                    homeFragment.scrollToTop()
                if (index == 1)
                    projectFragment.scrollToTop()
            }

            R.id.iv_svga -> {
                iv_svga.stopAnimation()
                iv_svga.visibility = View.GONE
            }
            R.id.iv_search_back -> SearchViewUtils.handleToolBar(applicationContext, cardview_search, et_search)
            R.id.iv_search -> {
                SearchViewUtils.handleToolBar(applicationContext, cardview_search, et_search)
                isSearching = true
            }
            R.id.tv_search_clear -> {
                searchRecordAdapter.data.clear()
                searchRecordAdapter.notifyDataSetChanged()
                DBManager.getInstance(this).deleteAll()
            }
            R.id.iv_speech_search -> {
                KeyboardUtils.hideKeyboard(et_search)
                requestRecordAudioPermission()
            }
        }

    }

    fun requestPermission() {
        requestPermission(this, object : PermissionUtil.RequestPermissionCallBack {

            override fun granted() {

            }

            override fun denied() {}
        }, *arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE))
    }

    public override fun onRestart() {
        super.onRestart()
        //跳转到设置界面后返回，重新检查权限
        requestPermission()
    }

    public override fun onStart() {
        super.onStart()
        val recordList = DBManager.getInstance(this).queryUserList()
        searchRecordAdapter.data.clear()
        searchRecordAdapter.addData(recordList)

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if (isSearching) {
                SearchViewUtils.handleToolBar(applicationContext, cardview_search, et_search)
                isSearching = false
                return false
            }
            if (System.currentTimeMillis() - exitTime > 2000) {
                //弹出提示，可以有多种方式
                Toast.makeText(applicationContext, getString(R.string.press_exit), Toast.LENGTH_SHORT).show()
                exitTime = System.currentTimeMillis()
            } else {
                finish()
            }
            return true
        }

        return super.onKeyDown(keyCode, event)
    }


    private fun loadSVGAAnimation() {
        val parser = SVGAParser(this)
        resetDownloader(parser)
        try {
            parser.parse(URL("https://github.com/yyued/SVGA-Samples/blob/master/kingset.svga?raw=true"), object : SVGAParser.ParseCompletion {
                override fun onComplete(videoItem: SVGAVideoEntity) {
                    val drawable = SVGADrawable(videoItem, requestDynamicItemWithSpannableText())
                    iv_svga.setImageDrawable(drawable)
                    iv_svga.startAnimation()

                    Handler().postDelayed({
                        /**
                         * 要执行的操作
                         */
                        /**
                         * 要执行的操作
                         */
                        iv_svga.stopAnimation()
                        iv_svga.visibility = View.GONE
                    }, 5000)

                }

                override fun onError() {

                }
            })
        } catch (e: Exception) {
            print(true)
        }

    }

    /**
     * 你可以设置富文本到 ImageKey 相关的元素上
     * 富文本是会自动换行的，不要设置过长的文本
     *
     * @return
     */
    private fun requestDynamicItemWithSpannableText(): SVGADynamicEntity {
        val dynamicEntity = SVGADynamicEntity()
        val spannableStringBuilder = SpannableStringBuilder(getString(R.string.welcome_learn_android))
        spannableStringBuilder.setSpan(ForegroundColorSpan(Color.YELLOW), 0, 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        val textPaint = TextPaint()
        textPaint.color = Color.WHITE
        textPaint.textSize = 28f
        dynamicEntity.setDynamicText(StaticLayout(
                spannableStringBuilder,
                0,
                spannableStringBuilder.length,
                textPaint,
                0,
                android.text.Layout.Alignment.ALIGN_CENTER,
                1.0f,
                0.0f,
                false
        ), "banner")
        dynamicEntity.setDynamicDrawer({ canvas, frameIndex ->
            val aPaint = Paint()
            aPaint.color = Color.WHITE
            canvas.drawCircle(50f, 54f, (frameIndex % 5).toFloat(), aPaint)
            false
        }, "banner")
        return dynamicEntity
    }

    /**
     * 设置下载器，这是一个可选的配置项。
     *
     * @param parser
     */
    private fun resetDownloader(parser: SVGAParser) {
        parser.fileDownloader = object : SVGAParser.FileDownloader() {
            override fun resume(url: URL, complete: Function1<InputStream, Unit>, failure: Function1<Exception, Unit>) {
                Thread(Runnable {
                    val client = OkHttpClient()
                    val request = Request.Builder().url(url).get().build()
                    try {
                        val response = client.newCall(request).execute()
                        complete.invoke(response.body()!!.byteStream())
                    } catch (e: IOException) {
                        e.printStackTrace()
                        failure.invoke(e)
                    }
                }).start()
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: RestartMainEvent) {
        val intent = intent
        overridePendingTransition(0, 0)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
        event.activity.finish()
        SPUtils.setParam(this@MainActivity, "isRestartMain", true)

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: ChangeNightEvent) {
        val isNightMode = SPUtils.getParam(this, "nightMode", false) as Boolean
        if (isNightMode) {
            title_view_divider.visibility = View.VISIBLE
        } else {
            title_view_divider.visibility = View.GONE
        }
        setStatusBar()
    }

    public override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun initPresenter(): MainActivityPresenter {
        return MainActivityPresenter()
    }

    override fun loadData() {
        mPresenter!!.getHotKey()
    }


    override fun showHotKey(list: List<HotKeyData>) {
        addFolowLayoutView(list)

    }

    private fun addFolowLayoutView(list: List<HotKeyData>) {
        val length = list.size
        for (i in list.indices) {
            val hotKeyData = list[i]
            val ranHeight = ScreenUtils.dp2px(this, 30)
            val lp = ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ranHeight)
            lp.setMargins(ScreenUtils.dp2px(this, 10), 0, ScreenUtils.dp2px(this, 10), 0)
            val tv = TextView(this)
            tv.setPadding(ScreenUtils.dp2px(this, 15), 0, ScreenUtils.dp2px(this, 15), 0)
            tv.setTextColor(Color.parseColor("#FF3030"))
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            val index = (Math.random() * length).toInt()
            tv.text = hotKeyData.name
            tv.gravity = Gravity.CENTER_VERTICAL
            tv.setLines(1)
            tv.setBackgroundResource(R.drawable.bg_tag)
            flowlayout.addView(tv, lp)
            tv.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("key", tv.text.toString())
                SearchResultActivity.startActivity(this@MainActivity, bundle)
                val dbManager = DBManager.getInstance(this@MainActivity)
                val searchRecord = SearchRecord()
                searchRecord.name = tv.text.toString()
                dbManager.insertUser(searchRecord)
            }
        }
        flowlayout.relayoutToAlign()
    }

    fun requestRecordAudioPermission() {
        requestPermission(this, object : PermissionUtil.RequestPermissionCallBack {
            override fun granted() {
                // 使用SpeechRecognizer对象，可根据回调消息自定义界面；这种方式主要是考虑到了，没有听写Dialog的时候，进行的听写监听
                mIat = SpeechRecognizer.createRecognizer(this@MainActivity, mInitListener)
                Log.i(TAG, "onCreate: mIat == null ?" + mIat)
                //SpeechRecognizer对象 null 的原因：一、 so 文件放错了位置 二、so文件与自己的SDK不匹配 3、Application中没有配置好appid
                // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
                // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置显示RecognizerDialog需要的布局文件和图片资源
                mIatDialog = RecognizerDialog(this@MainActivity, mInitListener)
                // 移动数据分析，收集开始听写事件
                FlowerCollector.onEvent(this@MainActivity, "iat_recognize")

                et_search.setText("")// 清空显示内容
                mIatResults.clear()
                // 设置参数
                setParam()

                // 显示听写对话框
                mIatDialog.setListener(mRecognizerDialogListener)
                mIatDialog.show()
                Utils.showToast(getString(R.string.begin_speech), true, Gravity.BOTTOM)
            }

            override fun denied() {}
        }, *arrayOf(Manifest.permission.RECORD_AUDIO))
    }

    fun setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null)

        // 设置听写引擎  注意：这里我只设置云端的方式！后面再考虑本地和混合的类型
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mCLOUDType)
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json")

        // 设置语言
        mIat.setParameter(SpeechConstant.LANGUAGE, "cn")
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin")

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000")

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000")

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "0")

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav")
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory().toString() + "/msc/iat.wav")
    }

    /**
     * 成功时显示说话的文字
     *
     * @param results
     */
    private fun printResult(results: RecognizerResult) {
        val text = JsonParser.parseIatResult(results.resultString)

        lateinit var sn: String
        // 读取json结果中的sn字段
        try {
            val resultJson = JSONObject(results.resultString)
            sn = resultJson.optString("sn")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        mIatResults[sn] = text

        val resultBuffer = StringBuffer()
        for (key in mIatResults.keys) {
            resultBuffer.append(mIatResults[key])
        }

        et_search.setText(resultBuffer.toString())
        //考虑到TextView只能显示文字 ，后面还要测试文字转语音，所以换EditText控件
        et_search.setSelection(et_search.length())
        KeyboardUtils.showKeyboard(et_search)
        LogUtil.d(TAG, "printResult(results)------------resultBuffer.toString():" + resultBuffer.toString())
        if (et_search.text.toString().length > 0 && "SearchResultActivity" != Utils.getTopActivity(this))
            beginSearch()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // 在这里添加屏幕切换后的操作
    }
}
