package com.tunein.dfpaudiosample.inject.components;


import com.tunein.dfpaudiosample.inject.AppModule;
import com.tunein.dfpaudiosample.ui.fragments.VideoPrerollFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Interface extension for client side that ties client side classes with AppComponent and allows them use injectinos.
 */
@Singleton
@Component(modules = {AppModule.class})
public interface SampleAppComponent extends AppComponent {

    void inject(VideoPrerollFragment fragment);
}
