package com.elabel.api.mvp.model.service;

import com.elabel.api.mvp.model.entity.BleData;
import com.elabel.api.mvp.model.entity.ESLTagType;
import com.elabel.api.mvp.model.entity.PageBody;
import com.elabel.api.mvp.model.entity.QueryTagEntity;
import com.elabel.api.mvp.model.entity.ResponseBase;
import com.elabel.api.mvp.model.entity.SysTag;
import com.elabel.api.mvp.model.entity.TokenEntity;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiService {
    /**
     * 登陆获取授权
     * @return
     */
    @POST("api/login")
    Observable<ResponseBase<String>> token(@Body TokenEntity tokenEntity);


    /**
     * 标签类型查询
     * @return
     */
    @GET("api/data/queryTagType")
    Observable<ResponseBase<List<ESLTagType>>> queryTagType();


    /**
     * 添加标签
     * @return
     */
    @POST("api/tag/add")
    Observable<ResponseBase<String>> addTag(@Body SysTag sysTag);


    /**
     * 获取点阵数据包(发送图片)
     * @param data
     * @param file
     * @return
     */
    @Multipart
    @POST("api/tag/get_ble_data")
    Observable<ResponseBase<BleData>> getBleImagePack(@PartMap Map<String, RequestBody> data, @Part MultipartBody.Part file);


    @GET("api/tag/DeleteTag/{mac}")
    Observable<ResponseBase<String>> deleteTag(@Path("mac") String mac);

    /**
     * 查询标签
     * @return
     */
    @POST("api/tag/queryList")
    Observable<ResponseBase<PageBody<SysTag>>> queryTagList(@Body QueryTagEntity queryTagEntity);
}
