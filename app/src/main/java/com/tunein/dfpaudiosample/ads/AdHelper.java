package com.tunein.dfpaudiosample.ads;

import android.support.annotation.NonNull;

import com.tunein.dfpaudiosample.adapters.VideoAdAdapter;
import com.tunein.dfpaudiosample.inject.components.VideoAdComponent;
import com.tunein.dfpaudiosample.interfaces.AdResponseListener;
import com.tunein.dfpaudiosample.interfaces.IAdControl;
import com.tunein.dfpaudiosample.interfaces.IInjectableFactory;
import com.tunein.dfpaudiosample.interfaces.VideoAdResponseListener;

import javax.inject.Inject;


/**
 * Main class for dealing with Ad logic. Should be POJO and have not any exact implementation of
 * particular Ad serving. This class will decide based on conditions which Ad to load.
 */
public class AdHelper {

    public static final String NOW_PLAYING = "NowPlaying";

    @Inject
    IInjectableFactory mMyFactory;

    private VideoAdAdapter mVideoAdAdapter;

    public AdHelper(@NonNull VideoAdComponent appComponent) {
        appComponent.inject(this);
        mVideoAdAdapter = mMyFactory.createVideoAdAdapter(appComponent);
    }

    /**
     * Requests an Ad based on screenName parameter.
     * @param screenName where ad will be displayed
     * @param listener that will provide Ad container and listen when ad will finish loading
     * @return IAdControl interface to communicate with Ad implementation
     */
    public IAdControl requestAd(String screenName, AdResponseListener listener) {
        if (screenName.equals(NOW_PLAYING)) {
            VideoAdResponseListener responseListener = (VideoAdResponseListener) listener;
            return mVideoAdAdapter.requestAd(responseListener);
        }
        return null;
    }
}
