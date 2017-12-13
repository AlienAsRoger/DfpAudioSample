package com.tunein.dfpaudiosample.adapters;


import android.util.Log;

import com.tunein.dfpaudiosample.inject.components.VideoAdComponent;
import com.tunein.dfpaudiosample.interfaces.IVideoAdControl;
import com.tunein.dfpaudiosample.interfaces.IVideoAdListener;
import com.tunein.dfpaudiosample.interfaces.IVideoAdPlayerController;
import com.tunein.dfpaudiosample.interfaces.IVideoAdPlayerView;
import com.tunein.dfpaudiosample.interfaces.VideoAdResponseListener;
import com.tunein.dfpaudiosample.utils.VastTagUtil;

import javax.inject.Inject;

/**
 * Video Ad Adapter that is responsible for requesting and loading Ad from provider into the given
 * view via {@link VideoAdResponseListener#addAdViewToContainer(Object)}
 */
public class VideoAdAdapter implements IVideoAdListener, IVideoAdControl {

    private static final String VAST_TAG = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/"
            + "single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast"
            + "&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct"
            + "%3Dlinear&correlator=";


    @Inject
    IVideoAdPlayerView mPlayerView;

    @Inject
    IVideoAdPlayerController mVideoPlayerController;

    private VideoAdResponseListener mListener;
    private boolean mIsSuccessfulRequest;

    public VideoAdAdapter(VideoAdComponent appComponent) {
        appComponent.inject(this);
    }

    public IVideoAdControl requestAd(VideoAdResponseListener listener) {
        mListener = listener;

        if (mPlayerView == null || mVideoPlayerController == null) {
            Log.d("TEST", "requestAd: mPlayerView or mVideoPlayerController is null, can't attach to container");
            return this;
        }
        // add video ad mPlayerView to container view
        mListener.addAdViewToContainer(mPlayerView);

        // set tag and make request
        String adUnitId = listener.getAdUnitId();
        String sizes = listener.getVideoSupportedSizes();

//        String tag = VastTagUtil.createVastUrlFromUnitId(adUnitId, "", sizes);
        String tag = VAST_TAG;
        Log.d("TEST", "requestAd: loading video ad request tag = " + tag);

        mVideoPlayerController.setAdTagUrl(tag);

        mIsSuccessfulRequest = mVideoPlayerController.requestAndPlayAds(this);

        return this;
    }

    @Override
    public void onPlayClicked() {
        if (mVideoPlayerController != null) {
            mVideoPlayerController.resume();
        }
    }

    @Override
    public void onPauseClicked() {
        if (mVideoPlayerController != null) {
            mVideoPlayerController.pause();
        }
    }

    @Override
    public void onStopClicked() {
        if (mVideoPlayerController != null) {
            mVideoPlayerController.stop();
        }
    }

    @Override
    public int getVideoPositionMs() {
        return mVideoPlayerController.getCurrentTimeMs();
    }

    @Override
    public int getVideoDurationMs() {
        return mVideoPlayerController.getDurationTimeMs();
    }

    @Override
    public int getVideoBufferPercentage() {
        return mVideoPlayerController.getBufferedPercentage();
    }

    @Override
    public void onAdLoaded() {
        mListener.onAdLoaded();
    }

    @Override
    public void onAdStarted() {
        mListener.onVideoAdStarted();
    }

    @Override
    public void onAdFinished() {
        mListener.onVideoAdFinished();
    }

    @Override
    public void resumeContent() {
        mListener.resumeContent();

        mPlayerView = null;
        mVideoPlayerController = null;
    }

    @Override
    public void onAdLoadFailed(String message) {
        mListener.onAdLoadFailed(message);
    }

    @Override
    public void onAdClicked() {
        Log.d("TEST", "onAdClicked: ");
    }

    @Override
    public boolean isSuccessfulRequest() {
        return mIsSuccessfulRequest;
    }
}
