package com.tunein.dfpaudiosample.interfaces;

import android.view.ViewGroup;

/**
 * Local in Ads SDK interface to listen when Ad provider (i.e. IMA SDK) finishes playing video ad.
 */
public interface IVideoAdListener {

    void onAdLoaded();

    void onAdStarted();

    void onAdFinished();

    void onAdLoadFailed(String message);

    void onAdClicked();

    void resumeContent();

    ViewGroup getCompanionAdView();
}
