package com.tunein.dfpaudiosample.interfaces;

/**
 * Base interface class that provides container where we load an ad.
 */
public interface AdResponseListener {

    void onAdLoaded();

    void onAdLoadFailed(String message);

    void addAdViewToContainer(Object object);
}
