package com.tunein.dfpaudiosample.inject;

import android.support.annotation.NonNull;

import com.tunein.dfpaudiosample.adapters.VideoAdAdapter;
import com.tunein.dfpaudiosample.ads.AdHelper;
import com.tunein.dfpaudiosample.inject.components.VideoAdComponent;
import com.tunein.dfpaudiosample.interfaces.IInjectableFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Factory class that is responsible for creating classes through AppComponent. This class should
 * be used as Injection and reference as interface IInjectableFactory.
 */
@Singleton
public class InjectableFactory implements IInjectableFactory {

    @Inject
    public InjectableFactory() {

    }

    @NonNull
    @Override
    public AdHelper createAdHelper(@NonNull VideoAdComponent appComponent) {
        return new AdHelper(appComponent);
    }

    @NonNull
    @Override
    public VideoAdAdapter createVideoAdAdapter(@NonNull VideoAdComponent appComponent) {
        return new VideoAdAdapter(appComponent);
    }
}
