package com.tunein.dfpaudiosample.inject.components;


import com.tunein.dfpaudiosample.adapters.VideoAdAdapter;
import com.tunein.dfpaudiosample.ads.AdHelper;
import com.tunein.dfpaudiosample.inject.VideoAdModule;
import com.tunein.dfpaudiosample.inject.scopes.VideoAdScope;

import dagger.Subcomponent;

/**
 * VideoAdComponent responsible for providing VideoAdModule to mentioned injections
 */
@VideoAdScope
@Subcomponent(modules = {VideoAdModule.class})
public interface VideoAdComponent {

    void inject(AdHelper adHelper);

    void inject(VideoAdAdapter videoAdAdapter);
}
