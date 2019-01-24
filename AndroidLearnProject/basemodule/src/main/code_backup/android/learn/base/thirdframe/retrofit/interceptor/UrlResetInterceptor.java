package com.android.learn.base.thirdframe.retrofit.interceptor;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/*
实际的开发过程中，我们在网络请求中会添加一些公共参数，对于一些可变的公共参数，在缓存数据和访问缓存数据的
过程中需要删除，比如网络类型，有网络时其值为Wifi或4G等，无网络时可能为none, 这时访问缓存时就会因url不一致导致访问缓存失败
*/

public class UrlResetInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {


        Response response = chain.proceed(chain.request());
        HttpUrl newUrl = chain.request().url().newBuilder()
                .removeAllQueryParameters("network")
                .build(); // 缓存数据前删除可变的公共参数
        Request newRequest = chain.request().newBuilder()
                .url(newUrl)
                .build();
        return response.newBuilder()
                .request(newRequest)
                .removeHeader("Pragma")
                .header("Cache-Control", "public, max-age=" + 1)
                .build();


//        Request request = chain.request();
//        HttpUrl url = request.url();
//        String s = url.url().toString();
//        //———请求之前—–
//        Response  response = null;
//        //如果Url中没有包含androidxx关键字，则修改请求链接为http://www.androidxx.cn
//        if (s.contains("androidxx")) {
//            request = request.newBuilder().url("http://www.androidxx.cn").build();
//        }
//        response = chain.proceed(request);
//        //———请求之后————
//        return response;
    }
}