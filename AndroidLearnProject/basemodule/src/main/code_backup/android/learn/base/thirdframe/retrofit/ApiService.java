package com.android.learn.base.thirdframe.retrofit;


import com.android.learn.base.mmodel.BannerListData;
import com.android.learn.base.mmodel.BaseData;
import com.android.learn.base.mmodel.BaseResponse;
import com.android.learn.base.mmodel.FeedArticleListData;
import com.android.learn.base.mmodel.HotKeyData;
import com.android.learn.base.mmodel.RegisterLoginData;
import com.android.learn.base.mmodel.ProjectListData;
import com.android.learn.base.mmodel.TodoData;
import com.android.learn.base.mmodel.TreeBean;
import com.android.learn.base.mmodel.WxArticle;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;


public interface ApiService {


    @GET("project/list/{page}/json")
    Observable<ProjectListData> getProjectListData(@Path("page") int page, @Query("cid") int cid);

    @GET("banner/json")
    Observable<BannerListData> getBannerListData();

    @GET("article/list/{num}/json")
    Observable<BaseResponse<FeedArticleListData>> getFeedArticleList(@Path("num") int num);

    @GET("article/list/{num}/json")
    Observable<BaseResponse<FeedArticleListData>> getKnowledgeArticleList(@Path("num") int num, @Query("cid") int cid);

    @POST("user/login")
    @FormUrlEncoded
    Observable<RegisterLoginData> login(@Field("username") String username, @Field("password") String password);

    @GET("user/logout/json")
    Observable<BaseData> logout();

    @POST("user/register")
    @FormUrlEncoded
    Observable<RegisterLoginData> register(@Field("username") String username, @Field("password") String password, @Field("repassword") String repassword);

    @POST("lg/collect/{id}/json")
    Observable<BaseData> addCollectArticle(@Path("id") int id);

    @GET("lg/collect/list/{page}/json")
    Observable<BaseResponse<FeedArticleListData>> getCollectList(@Path("page") int page);

    @POST("lg/uncollect_originId/{id}/json")
    @FormUrlEncoded
    Observable<BaseData> cancelCollectArticle(@Path("id") int id, @Field("originId") int originId);

    @GET("tree/json")
    Observable<BaseResponse<List<TreeBean>>> getKnowledgeTree();

    @GET("hotkey/json")
    Observable<BaseResponse<List<HotKeyData>>> getHotKey();

    @GET("wxarticle/chapters/json")
    Observable<BaseResponse<List<WxArticle>>> getWxArticle();

    @GET("wxarticle/list/{id}/{num}/json")
    Observable<BaseResponse<FeedArticleListData>> getWxArtileById(@Path("id") int id, @Path("num") int num);

    @POST("article/query/{num}/json")
    @FormUrlEncoded
    Observable<BaseResponse<FeedArticleListData>> search(@Path("num") int num, @Field("k") String key);

    @GET("lg/todo/list/{type}/json")
    Observable<BaseResponse<TodoData>> getTodoList(@Path("type") int type);

    @POST("lg/todo/listnotdo/{type}/json/{page}")
    Observable<BaseResponse<TodoData>> getListNotDone(@Path("type") int type, @Path("page") int page);

    @POST("lg/todo/listdone/{type}/json/{page}")
    Observable<BaseResponse<TodoData>> getListDone(@Path("type") int type, @Path("page") int page);

    @POST("lg/todo/add/json")
    Observable<BaseData> addTodo(@QueryMap Map<String, Object> map);

    @POST("lg/todo/update/{id}/json")
    Observable<BaseData> updateTodo(@Path("id") int id, @QueryMap Map<String, Object> map);

    @POST("lg/todo/delete/{id}/json")
    Observable<BaseData> deleteTodo(@Path("id") int id);

    //status: 0或1，传1代表未完成到已完成，反之则反之。
    @POST("lg/todo/done/{id}/json")
    Observable<BaseData> updateTodoStatus(@Path("id") int id, @Query("status") int status);
}
