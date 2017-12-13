package com.tunein.dfpaudiosample.inject.components;


import com.tunein.dfpaudiosample.inject.AppModule;
import com.tunein.dfpaudiosample.inject.VideoAdModule;
import com.tunein.dfpaudiosample.ui.fragments.BaseFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Main interface to communicate with classes that will use injections. Can be extended in client app or in tests for
 * additional implementations.
 */
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    void inject(BaseFragment baseFragment);

    VideoAdComponent plus(VideoAdModule videoAdModule);

}
