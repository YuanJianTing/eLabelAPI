package com.elabel.api.injection.module;

import com.elabel.api.MyApplication;
import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private MyApplication mApplication;

    public AppModule(MyApplication application) {
        mApplication = application;
    }
    @Singleton
    @Provides
    MyApplication providerMyApplication(){
        return mApplication;
    }

    @Singleton
    @Provides
    Gson providerGson(){
        return new Gson();
    }


}
