package com.tunein.dfpaudiosample.application;

import com.tunein.dfpaudiosample.inject.AppModule;
import com.tunein.dfpaudiosample.inject.components.AppComponent;
import com.tunein.dfpaudiosample.inject.components.DaggerSampleAppComponent;


/**
 * Application class used for injections
 */
public class SampleApplication extends BaseApplication {

    @Override
    protected AppComponent createAppComponent() {
        return DaggerSampleAppComponent.builder()
                .appModule(new AppModule())
                .build();
    }
}
