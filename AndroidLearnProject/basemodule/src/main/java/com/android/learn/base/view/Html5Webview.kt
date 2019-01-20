package com.android.learn.base.view

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Message
import android.support.annotation.RequiresApi
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

class Html5Webview @JvmOverloads constructor(private val context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : WebView(context, attrs, defStyleAttr) {
    private var progressView: ProgressView? = null//进度条

    init {
        init()
    }

    private fun init() {
        //初始化进度条
        progressView = ProgressView(context)
        progressView!!.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(context, 3f))
        //        progressView.setColor(Color.BLUE);
        //        progressView.setProgress(10);
        //把进度条加到Webview中
        addView(progressView)
        //初始化设置
        initWebSettings()
        webChromeClient = MyWebCromeClient()
        webViewClient = MyWebviewClient()
    }

    private fun initWebSettings() {
        val settings = settings
        //默认是false 设置true允许和js交互
        settings.javaScriptEnabled = true
        //  WebSettings.LOAD_DEFAULT 如果本地缓存可用且没有过期则使用本地缓存，否加载网络数据 默认值
        //  WebSettings.LOAD_CACHE_ELSE_NETWORK 优先加载本地缓存数据，无论缓存是否过期
        //  WebSettings.LOAD_NO_CACHE  只加载网络数据，不加载本地缓存
        //  WebSettings.LOAD_CACHE_ONLY 只加载缓存数据，不加载网络数据
        //Tips:有网络可以使用LOAD_DEFAULT 没有网时用LOAD_CACHE_ELSE_NETWORK
        settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        //开启 DOM storage API 功能 较大存储空间，使用简单
        settings.domStorageEnabled = true
        //开启 Application Caches 功能 方便构建离线APP 不推荐使用
        settings.setAppCacheEnabled(true)
        val cachePath = context.applicationContext.getDir("cache", Context.MODE_PRIVATE).path
        settings.setAppCachePath(cachePath)
        settings.setAppCacheMaxSize((5 * 1024 * 1024).toLong())
        //设置数据库缓存路径 存储管理复杂数据 方便对数据进行增加、删除、修改、查询 不推荐使用
        settings.databaseEnabled = true
        val dbPath = context.applicationContext.getDir("db", Context.MODE_PRIVATE).path
        settings.databasePath = dbPath
    }


    private inner class MyWebCromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            if (newProgress == 100) {
                //加载完毕进度条消失
                progressView!!.visibility = View.GONE
            } else {
                //更新进度
                progressView!!.setProgress(newProgress)
            }
            super.onProgressChanged(view, newProgress)
        }

        override fun onReceivedTitle(view: WebView, title: String) {
            Log.e("TTT", "title is $title")
            super.onReceivedTitle(view, title)
        }

        override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
            return super.onJsAlert(view, url, message, result)
        }

        override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
            return super.onConsoleMessage(consoleMessage)
        }

        override fun onCreateWindow(view: WebView, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message): Boolean {
            return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
        }

        override fun onJsBeforeUnload(view: WebView, url: String, message: String, result: JsResult): Boolean {
            return super.onJsBeforeUnload(view, url, message, result)
        }

        override fun onJsConfirm(view: WebView, url: String, message: String, result: JsResult): Boolean {
            return super.onJsConfirm(view, url, message, result)
        }

        override fun onJsPrompt(view: WebView, url: String, message: String, defaultValue: String, result: JsPromptResult): Boolean {
            return super.onJsPrompt(view, url, message, defaultValue, result)
        }

        override fun onCloseWindow(window: WebView) {
            super.onCloseWindow(window)
        }

        override fun onShowFileChooser(webView: WebView, filePathCallback: ValueCallback<Array<Uri>>, fileChooserParams: WebChromeClient.FileChooserParams): Boolean {
            return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
        }
    }

    private inner class MyWebviewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            Log.e("TTT", "shouldOverrideUrlLoading 0")
            val uri = Uri.parse(url)
            val scheme = uri.scheme
            if (TextUtils.isEmpty(scheme)) return true
            if (scheme == "nativeapi") {
                //如定义nativeapi://showImg是用来查看大图，这里添加查看大图逻辑
                return true
            } else if (scheme == "http" || scheme == "https") {
                //处理http协议
                if (Uri.parse(url).host == "www.example.com") {
                    // 内部网址，不拦截，用自己的webview加载
                    return false
                } else {
                    //跳转外部浏览器
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    context.startActivity(intent)
                    return true
                }
            }
            return super.shouldOverrideUrlLoading(view, url)
        }

        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            Log.e("TTT", "shouldOverrideUrlLoading 1")
            return super.shouldOverrideUrlLoading(view, request)
        }


        override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
            Log.e("TTT", "shouldInterceptRequest 0 url is $url")
            //回调发生在子线程中,不能直接进行UI操作
            return super.shouldInterceptRequest(view, url)
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
            Log.e("TTT", "shouldInterceptRequest 1 request url is " + request.url.toString())
            return super.shouldInterceptRequest(view, request)
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
            Log.e("TTT", "onPageStarted")
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView, url: String) {
            Log.e("TTT", "onPageFinished")
            super.onPageFinished(view, url)
        }

        override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
            Log.e("TTT", "onPageFinished")
            super.onReceivedError(view, request, error)
        }
    }

    /**
     * dp转换成px
     *
     * @param context Context
     * @param dp      dp
     * @return px值
     */
    private fun dp2px(context: Context, dp: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }
}