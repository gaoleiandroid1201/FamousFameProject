package com.android.learn.base.thirdframe.retrofit.interceptor;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class DefaultHeaderInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalrequest = chain.request();//原始request
        Headers headers = new Headers.Builder()
                .add("name", "jason")
                .add("age", "27")
                .add("token", "dfedsdfsdfffdd12dsef123sdfef1s2dfe")
                .build();//构造一个Headers
        Request request = originalrequest.newBuilder().headers(headers).build();//注意这行代码别写错了
        return chain.proceed(request);
    }
}