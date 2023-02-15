package com.elabel.api.injection.component;

import com.elabel.api.MyApplication;
import com.elabel.api.injection.module.ApiModule;
import com.elabel.api.injection.module.AppModule;
import com.elabel.api.mvp.model.service.ApiService;
import com.elabel.api.mvp.model.service.RepositoryManager;
import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, ApiModule.class})
public interface AppComponent {
    MyApplication getMyApplication();
    ApiService getApiService();
    RepositoryManager getRepositoryManager();
    Gson getGson();
}
