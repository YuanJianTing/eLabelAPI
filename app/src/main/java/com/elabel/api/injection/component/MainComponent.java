package com.elabel.api.injection.component;

import com.elabel.api.injection.ActivityScope;
import com.elabel.api.injection.module.MainModule;
import com.elabel.api.ui.activity.MainActivity;

import dagger.Component;

@ActivityScope
@Component(modules = {MainModule.class} ,dependencies = {AppComponent.class})
public interface MainComponent {
    void inject(MainActivity activity);
}
