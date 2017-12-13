package com.tunein.dfpaudiosample.inject;


import com.tunein.dfpaudiosample.interfaces.IInjectableFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Application level module
 */
@Module
public class AppModule {

    @Provides
    @Singleton
    IInjectableFactory provideMyFactory() {
        return new InjectableFactory();
    }
}
