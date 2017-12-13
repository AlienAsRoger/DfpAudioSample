package com.tunein.dfpaudiosample.interfaces;

import android.support.annotation.NonNull;

import com.tunein.dfpaudiosample.adapters.VideoAdAdapter;
import com.tunein.dfpaudiosample.ads.AdHelper;
import com.tunein.dfpaudiosample.inject.components.VideoAdComponent;


/**
 * Factory interface to create instances through AppComponent that can use injections
 */
public interface IInjectableFactory {

    @NonNull
    AdHelper createAdHelper(@NonNull VideoAdComponent appComponent);

    @NonNull
    VideoAdAdapter createVideoAdAdapter(@NonNull VideoAdComponent appComponent);
}
