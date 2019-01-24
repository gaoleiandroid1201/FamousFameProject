package com.android.learn.base.thirdframe.retrofit;


import com.android.learn.base.application.CustomApplication;
import com.android.learn.base.thirdframe.retrofit.interceptor.DefaultHeaderInterceptor;
import com.android.learn.base.thirdframe.retrofit.interceptor.HttpLoggingInterceptor;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;


public final class RetrofitDynamicProvider {
    private final Charset UTF8 = Charset.forName("UTF-8");
    private Retrofit mRetrofit;
    private OkHttpClient mOkHttpClient;
    private static volatile RetrofitDynamicProvider sInstance;
    private ApiService restService;
    public static String netCachePath;
//    public final String BASE_URL = "http://www.xuetangx.com/";

    private RetrofitDynamicProvider() {
    }


    public RetrofitDynamicProvider builder(String BASE_URL) {
        netCachePath = CustomApplication.context.getExternalFilesDir("net_cache").getAbsolutePath();
        if (mOkHttpClient == null) {
            mOkHttpClient = new OkHttpClient.Builder()
                    .addNetworkInterceptor(new HttpLoggingInterceptor())
//                    .addNetworkInterceptor(new HandleGzipInterceptor())
                    .addInterceptor(new DefaultHeaderInterceptor())//请求连接中添加头信息
//                    .addNetworkInterceptor(new OnlineCacheInterceptor())//有网缓存拦截器
//                    .addInterceptor(new OfflineCacheInterceptor())//无网缓存拦截器
//                    .cache(new Cache(new File(netCachePath), 50 * 10240 * 1024))//缓存路径和空间设置
//                    .addInterceptor(new RetryIntercepter(4))//重试
//                    .addInterceptor(new GzipRequestInterceptor())//开启Gzip压缩

//                    .addInterceptor(new ProgressInterceptor())//请求url的进度
//                    .addInterceptor(new TokenInterceptor())//token过期，自动刷新Token
//                    .addInterceptor(new SignInterceptor())//所有的接口，默认需要带上sign,timestamp2个参数
//                    .addNetworkInterceptor(new ParamsEncryptInterceptor())//参数加密,一般针对表单中的字段和值进行加密，防止中途第三方进行窥探和篡改
                    .cookieJar(new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(CustomApplication.context)))
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

        }
//        if (mRetrofit == null) {
        mRetrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .baseUrl(BASE_URL)
                .addConverterFactory(CustomizeGsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
//        }
        return sInstance;
    }

    public static RetrofitDynamicProvider getInstance() {
        if (sInstance == null) {
            synchronized (RetrofitDynamicProvider.class) {
                if (sInstance == null) {
                    sInstance = new RetrofitDynamicProvider();
                }
            }
        }
        return sInstance;
    }

    public <T> T createService(Class<T> tClass) {
        return mRetrofit.create(tClass);
    }


}