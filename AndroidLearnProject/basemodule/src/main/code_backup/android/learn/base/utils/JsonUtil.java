package com.android.learn.base.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * JSON字符换和对象间转换的工具类
 */
public class JsonUtil {

    private static final Gson GSON = new Gson();

    /**
     * 将json字符串转换成相应的对象
     *
     * @param jsonString json字符串
     * @param classOfT   对象类型的class
     * @param <T>        对象的类型
     * @return 转换后的对象
     * @throws JsonParseException if json is not a valid representation for an object of type classOfT
     */
    public static <T> T decode(String jsonString, Class<T> classOfT) throws RuntimeException {
        return GSON.fromJson(jsonString, classOfT);
    }

    /**
     * 将json字符串转换成相应的对象
     *
     * @param jsonString json字符串
     * @param typeOfT    对象类型的type
     * @param <T>        对象的类型
     * @return 转换后的对象
     * @throws JsonParseException if json is not a valid representation for an object of type typeOfT
     */
    public static <T> T decode(String jsonString, Type typeOfT) throws RuntimeException {
        return GSON.fromJson(jsonString, typeOfT);
    }

    /**
     * 将已经从json转换后的对象（通常是一个Map）转换成对应Bean的对象实例
     *
     * @param object  一个转json换中的过程对象，通常是一个Map，若传值为null，则返回null
     * @param typeOfT 要转换的对象类型的type
     * @param <T>     要转换的对象类型
     * @return 转换后的对象
     * @throws JsonParseException if object is not a valid representation for an object of type typeOfT
     */
    public static <T> T decode(Object object, Type typeOfT) throws RuntimeException {
        String jsonString = GSON.toJson(object);
        return GSON.fromJson(jsonString, typeOfT);
    }

    /**
     * 将一个JavaBean转换成json字符串
     *
     * @param object 待转换的对象
     * @return json字符串
     * @throws JsonParseException if there was a problem while parsing object.
     */
    public static String encode(Object object) throws RuntimeException {
        return GSON.toJson(object);
    }


    /**
     * 获取JsonObject
     *
     * @param json
     * @return
     */
    public static JsonObject parseJson(String json) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObj = parser.parse(json).getAsJsonObject();
        return jsonObj;
    }

    /**
     * 根据javaBean转json字符串再将字符串转Map对象
     *
     * @param object
     * @return
     */
    public static Map<String, Object> javaBeanToMap(Object object) {
        String strJson = encode(object);
        return toMap(parseJson(strJson));
    }

    /**
     * 将JSONObjec对象转换成Map-List集合
     *
     * @param json
     * @return
     */
    public static Map<String, Object> toMap(JsonObject json) {
        Map<String, Object> map = new HashMap<String, Object>();
        Set<Map.Entry<String, JsonElement>> entrySet = json.entrySet();
        for (Iterator<Map.Entry<String, JsonElement>> iter = entrySet.iterator(); iter.hasNext(); ) {
            Map.Entry<String, JsonElement> entry = iter.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof JsonArray)
                map.put((String) key, toList((JsonArray) value));
            else if (value instanceof JsonObject)
                map.put((String) key, toMap((JsonObject) value));
            else
                map.put((String) key, value);
        }
        return map;
    }

    /**
     * 将JSONArray对象转换成List集合
     *
     * @param json
     * @return
     */
    public static List<Object> toList(JsonArray json) {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < json.size(); i++) {
            Object value = json.get(i);
            if (value instanceof JsonArray) {
                list.add(toList((JsonArray) value));
            } else if (value instanceof JsonObject) {
                list.add(toMap((JsonObject) value));
            } else {
                list.add(value);
            }
        }
        return list;
    }

    /**
     * Json解析公用方法 数组
     *
     * @param json  解析参数
     * @param token 解析的类型 new TypeToken<T>(){}
     *              ArrayList<T> temp = ParserJson.fromJson(data, new TypeToken<ArrayList<T>>(){});
     * @return
     */
    public static <T> T fromJson(JsonElement json, TypeToken<T> token) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, token.getType());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Json解析公用方法 数组
     *
     * @param json 解析参数
     * @param type 解析的类型 new TypeToken<T>(){}.getType()
     *             ArrayList<T> temp = ParserJson.fromJson(data, new TypeToken<ArrayList<T>>(){});
     * @return
     */
    public static <T> T fromJson(JsonElement json, Type type) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, type);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Json解析公用方法 单个类
     *
     * @param json 解析参数
     * @param t    解析的类。 class
     * @return
     */
    public static <T> T fromJson(JsonElement json, Class<T> t) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, t);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Json解析公用方法 数组
     *
     * @param json  解析参数
     * @param token 解析的类型 new TypeToken<T>(){}
     *              ArrayList<T> temp = ParserJson.fromJson(data, new TypeToken<ArrayList<T>>(){});
     * @return
     */
    public static <T> T fromJson(String json, TypeToken<T> token) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, token.getType());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Json解析公用方法 单个类
     *
     * @param json 解析参数
     * @param t    解析的类。 class
     * @return
     */
    public static <T> T fromJson(String json, Class<T> t) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, t);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 把数据转换成Json结构
     *
     * @param mType Object的数据，支持所有类型
     * @return
     */
    public static String toJson(Object mType) {
        Gson mGson = new Gson();
        String mResult = mGson.toJson(mType);
        return mResult;
    }
}

