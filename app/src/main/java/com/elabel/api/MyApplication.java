package com.elabel.api;

import android.app.Application;

import com.elabel.api.injection.component.AppComponent;
import com.elabel.api.injection.component.DaggerAppComponent;
import com.elabel.api.injection.module.ApiModule;
import com.elabel.api.injection.module.AppModule;

public class MyApplication extends Application {

    private static AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this))
                .apiModule(new ApiModule()).build();

    }

    public static AppComponent getAppComponent() {
        return mAppComponent;
    }
}
