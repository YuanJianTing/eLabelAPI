package com.elabel.api.mvp.model.service;


import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.elabel.api.MyApplication;
import com.elabel.api.mvp.model.entity.BleData;
import com.elabel.api.mvp.model.entity.ESLTagType;
import com.elabel.api.mvp.model.entity.PageBody;
import com.elabel.api.mvp.model.entity.QueryTagEntity;
import com.elabel.api.mvp.model.entity.ResponseBase;
import com.elabel.api.mvp.model.entity.SysTag;
import com.elabel.api.mvp.model.entity.TokenEntity;
import com.elabel.api.utils.PreferencesHelper;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class RepositoryManager {
    private final ApiService apiService;

    public RepositoryManager(ApiService apiService) {
        this.apiService = apiService;
    }


    public Observable<ResponseBase<String>> token(String loginId, String password){
        String client= "client name";
        TokenEntity tokenEntity=new TokenEntity();
        tokenEntity.setPassword(password);
        tokenEntity.setUsername(loginId);
        tokenEntity.setClientID(client);
        tokenEntity.setArea(0);
        return apiService.token(tokenEntity)
                .flatMap(responseBase -> {
                    if(responseBase!=null&&responseBase.getCode()==0)
                        PreferencesHelper.setString(MyApplication.getAppComponent().getMyApplication(),"TOKEN",responseBase.getBody());
                    return Observable.just(responseBase);
                });
    }

    /**
     * 获取系统支持的标签类型列表
     * ESLTagType::tagType 位mac中的前两位；如【3D000002AE44】--》3D
     * @return
     */
    public Observable<List<ESLTagType>> queryTagType(){
       return apiService.queryTagType()
                    .flatMap(new BaseResponseFunc<>());
    }


    public Observable<ResponseBase<BleData>> getBleImagePack(byte[] image, String mac){
        RequestBody requestFile = RequestBody.create(image,MediaType.parse("image/jpg"));
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", "U.jpg", requestFile);
        Map<String, RequestBody> map = new HashMap<>();
        map.put("mac", RequestBody.create( mac,MediaType.parse("text/plain")));
        return apiService.getBleImagePack(map,body);
    }

    public Observable<ResponseBase<String>> addTag(String mac){
        SysTag sysTag=new SysTag();
        sysTag.setActivate(false);
        sysTag.setMac(mac);
        sysTag.setVersion("");
        return apiService.addTag(sysTag);
    }

    public Observable<ResponseBase<String>> deleteTag(String mac){
        return apiService.deleteTag(mac);
    }

    public Observable<PageBody<SysTag>> queryTagList(int pageIndex, int pageSize, String keyword, String tagType){
        QueryTagEntity queryTagEntity=new QueryTagEntity();
        queryTagEntity.setTagType(tagType);
        queryTagEntity.setPageIndex(pageIndex);
        queryTagEntity.setPageSize(pageSize);
        queryTagEntity.setKeyword(keyword);
        return apiService.queryTagList(queryTagEntity)
                .flatMap(new BaseResponseFunc<>());
    }
}
