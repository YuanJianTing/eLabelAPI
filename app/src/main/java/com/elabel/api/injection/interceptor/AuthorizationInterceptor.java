package com.elabel.api.injection.interceptor;


import android.text.TextUtils;

import com.elabel.api.MyApplication;
import com.elabel.api.app.Constants;
import com.elabel.api.mvp.model.entity.ResponseBase;
import com.elabel.api.mvp.model.service.Api;
import com.elabel.api.utils.PreferencesHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthorizationInterceptor implements Interceptor {
    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        //获取原始的originalRequest
        Request request = chain.request();

        Request.Builder builder = request.newBuilder();
        String oldUrl = request.url().toString();
        if(!TextUtils.isEmpty(Constants.token))
            builder.addHeader("Authorization","Bearer "+Constants.token);

        return  handlerResponse(chain.proceed(builder.build()),chain,oldUrl);
    }

    private Response handlerResponse(Response response,Chain chain,String oldUrl) throws IOException {
        if(response.code()==401){
            //如果为登陆则直接返回
            if(oldUrl.equalsIgnoreCase("login"))
                return response;

            //刷新token
            boolean result= refreshToken();
            if(result) {
                //重新提交
                Request newRequest = chain.request()
                        .newBuilder()
                        .url(oldUrl)
                        .header("Authorization", "Bearer " + Constants.token)
                        .build();
                return chain.proceed(newRequest);
            }
        }
        return response;
    }


    private boolean refreshToken(){
        String param=String.format("{\"token\":\"%s\"}",Constants.token);

        RequestBody requestBody=RequestBody.create(param,MediaType.get("application/json"));
        final Request request = new Request.Builder()
                .url(TextUtils.concat(Api.DEFAULT_URL,"api/refresh").toString())
                .post(requestBody)
                .build();

        OkHttpClient httpClient=new OkHttpClient();
        try {
            Response response= httpClient.newCall(request).execute();
            if(response.code()==200){
                String json= response.body().string();
                Gson gson=new Gson();
                Type type=new TypeToken<ResponseBase<String>>(){}.getType();
                ResponseBase<String> tokenInfoResponseBase=gson.fromJson(json,type);
                if(tokenInfoResponseBase.getCode()==0&&tokenInfoResponseBase.getBody()!=null){
                    Constants.token=tokenInfoResponseBase.getBody();
                    PreferencesHelper.setString(MyApplication.getAppComponent().getMyApplication(),"TOKEN",Constants.token);
                    return true;
                }else {
                    Constants.token=null;
                    PreferencesHelper.setString(MyApplication.getAppComponent().getMyApplication(), "TOKEN", "");
                }
                gson=null;
            }else{
                Constants.token=null;
                PreferencesHelper.setString(MyApplication.getAppComponent().getMyApplication(),"TOKEN","");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
