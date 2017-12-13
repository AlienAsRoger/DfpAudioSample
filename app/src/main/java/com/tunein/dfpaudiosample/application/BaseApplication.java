package com.tunein.dfpaudiosample.application;

import android.app.Application;

import com.tunein.dfpaudiosample.inject.components.AppComponent;


/**
 * If you want to use Injections, extent this application and call {@link #getAppComponent()} to your factory.
 */
public abstract class BaseApplication extends Application {

    private AppComponent appComponent;

    protected abstract AppComponent createAppComponent();

    @Override
    public void onCreate() {
        super.onCreate();

        if (appComponent == null) {
            appComponent = createAppComponent();
        }
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

}
