package com.android.learn.base.thirdframe.retrofit;

import com.android.learn.base.utils.LogUtil;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * @param <T>
 * @author lqx
 */
public class CustomizeGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private final Gson gson;
    private final TypeAdapter<T> adapter;
    String TAG="CustomizeGsonResponseBodyConverter";

    CustomizeGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {

        String responseBodyStr = value.string();
        LogUtil.d(TAG,"responseBodyStr---------------"+responseBodyStr);


        return adapter.fromJson(responseBodyStr);
    }
}
