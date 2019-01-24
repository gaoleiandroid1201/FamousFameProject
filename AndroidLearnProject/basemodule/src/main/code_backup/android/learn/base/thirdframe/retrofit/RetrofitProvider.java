package com.android.learn.base.thirdframe.retrofit;


import com.android.learn.base.application.CustomApplication;
import com.android.learn.base.thirdframe.retrofit.interceptor.HttpLoggingInterceptor;
import com.android.learn.base.thirdframe.retrofit.interceptor.OfflineCacheInterceptor;
import com.android.learn.base.thirdframe.retrofit.interceptor.OnlineCacheInterceptor;
import com.android.learn.base.thirdframe.retrofit.interceptor.RetryIntercepter;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public final class RetrofitProvider {

    private Retrofit mRetrofit;
    private OkHttpClient mOkHttpClient;
    private static volatile RetrofitProvider sInstance;
    private ApiService restService;
    public static String netCachePath;
    public final String BASE_URL = "http://www.wanandroid.com/";
    public PersistentCookieJar persistentCookieJar;
    public SharedPrefsCookiePersistor sharedPrefsCookiePersistor;

    private RetrofitProvider() {
    }


    public RetrofitProvider builder() {
        netCachePath = CustomApplication.context.getExternalFilesDir("net_cache").getAbsolutePath();
        if (mOkHttpClient == null) {
            sharedPrefsCookiePersistor=new SharedPrefsCookiePersistor(CustomApplication.context);
            persistentCookieJar=new PersistentCookieJar(new SetCookieCache(), sharedPrefsCookiePersistor);
            mOkHttpClient = new OkHttpClient.Builder()
                    .addNetworkInterceptor(new HttpLoggingInterceptor())
                    .addNetworkInterceptor(new OnlineCacheInterceptor())//有网缓存拦截器
                    .addInterceptor(new OfflineCacheInterceptor())//无网缓存拦截器
                    .cache(new Cache(new File(netCachePath), 50 * 10240 * 1024))//缓存路径和空间设置
                    .addInterceptor(new RetryIntercepter(4))//重试
//                    .addInterceptor(new GzipRequestInterceptor())//开启Gzip压缩

//                    .addInterceptor(new DefaultHeaderInterceptor())//请求连接中添加头信息
//                    .addInterceptor(new ProgressInterceptor())//请求url的进度
//                    .addInterceptor(new TokenInterceptor())//token过期，自动刷新Token
//                    .addInterceptor(new SignInterceptor())//所有的接口，默认需要带上sign,timestamp2个参数
//                    .addNetworkInterceptor(new ParamsEncryptInterceptor())//参数加密,一般针对表单中的字段和值进行加密，防止中途第三方进行窥探和篡改
                    .cookieJar(persistentCookieJar)
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .writeTimeout(5, TimeUnit.SECONDS)
                    .build();

        }
        if (mRetrofit == null) {
            mRetrofit = new Retrofit.Builder()
                    .client(mOkHttpClient)
                    .baseUrl(BASE_URL)
                    .addConverterFactory(CustomizeGsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return sInstance;
    }

    public static RetrofitProvider getInstance() {
        if (sInstance == null) {
            synchronized (RetrofitProvider.class) {
                if (sInstance == null) {
                    sInstance = new RetrofitProvider();
                }
            }
        }
        return sInstance;
    }

    public <T> T createService(Class<T> tClass) {
        return mRetrofit.create(tClass);
    }

    public ApiService getApiService() {
        if (restService == null)
            restService = mRetrofit.create(ApiService.class);
        return restService;
    }
}