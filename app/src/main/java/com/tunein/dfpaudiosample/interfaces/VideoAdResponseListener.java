package com.tunein.dfpaudiosample.interfaces;


/**
 * Extension for {@link AdResponseListener} for Video Ads. Should be used to communicate from client
 * side to Ads SDK and Ad provider for the ad itself.
 */
public interface VideoAdResponseListener extends AdResponseListener {

    void onVideoAdStarted();

    void resumeContent();

    void onVideoAdFinished();

    String getAdUnitId();

    String getVideoSupportedSizes();

}
