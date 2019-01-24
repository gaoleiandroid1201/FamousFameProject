package com.android.learn.base.thirdframe.retrofit.interceptor;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.util.Pair;

import com.android.learn.base.utils.LogUtil;
import com.android.learn.base.utils.Utils;
import com.gaolei.basemodule.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;



public final class SignInterceptor implements Interceptor {

    private static final String MD5KEY = "base64:FgBvWS7+m3BPRnKemuoOwXEf7kvldmM+VOmJS5Iccxs=";
    private Context mContext;

    public SignInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        //header add version
        Request.Builder requestBuilder = request.newBuilder();
        requestBuilder.addHeader("version", BuildConfig.VERSION_NAME);
        requestBuilder.addHeader("platform", "ANDROID");

        if (!request.method().equals("POST")) {
            return chain.proceed(request);
        }

        JSONObject bodyJson = null;
        List<Pair<String, String>> params = new ArrayList<>();
        ArrayMap<String, String> arrayMap = new ArrayMap<>();

        FormBody body = null;
        try {
            body = (FormBody) request.body();
        } catch (ClassCastException c) {
        }

        if (body != null) {

            int size = body.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    params.add(new Pair<>(body.name(i), body.value(i)));
                }
            }
        } else {
            RequestBody requestBody = request.body();

            if (requestBody != null) {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);
                Charset charset = Charset.forName("UTF-8");
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(charset);
                }
                String paramsStr = buffer.readString(charset);
                if (BuildConfig.DEBUG) {
                    Log.i("HTTP", "request = " + paramsStr);
                }

                try {
                    bodyJson = new JSONObject(paramsStr);
                } catch (JSONException e) {
                    LogUtil.e("JSON", "JsonObject err");
                }

                if (bodyJson != null) {
                    Iterator<String> it = bodyJson.keys();
                    while (it.hasNext()) {
                        String key = it.next();
                        Object value = null;
                        try {
                            value = bodyJson.get(key);
                        } catch (JSONException e) {
                            LogUtil.e("JSON", "JsonObject get err");
                        }
                        if (key.equals("token")) {
                            arrayMap.put("token", String.valueOf(value));
                            arrayMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));

                            for (String strKey : arrayMap.keySet()) {
                                params.add(new Pair<>(strKey, arrayMap.get(strKey)));
                            }
                        } else {
                            params.add(new Pair<>(key, String.valueOf(value)));
                        }

                    }
                }

            }
        }

        params.add(new Pair<>("MD5Key", MD5KEY));
        Collections.sort(params, new Comparator<Pair<String, String>>() {
            @Override
            public int compare(Pair<String, String> lhs, Pair<String, String> rhs) {
                return lhs.second.compareTo(rhs.second);
            }
        });

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).second);
            if (i < params.size() - 1) {
                sb.append("");
            }
        }

        String sign = Utils.md5Encode(sb.toString());
        params.add(new Pair<>("sign", sign));

        for (Pair<String, String> pair : params) {
            if ((arrayMap.containsKey(pair.first) || pair.first.equals("sign")) && bodyJson != null) {
                try {
                    bodyJson.put(pair.first, pair.second);
                } catch (JSONException e) {
                    LogUtil.e("JSON", "JsonObject put err");
                }
            }
        }

        String postBodyString = bodyJson.toString();
        request = requestBuilder
                .post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"),
                        postBodyString))
                .build();

        return chain.proceed(request);
    }

}
