// Copyright 2014 Google Inc. All Rights Reserved.

package com.tunein.dfpaudiosample.videoplayer;

import android.content.Context;

import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdErrorEvent;
import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.AdsLoader;
import com.google.ads.interactivemedia.v3.api.AdsManager;
import com.google.ads.interactivemedia.v3.api.AdsManagerLoadedEvent;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;
import com.google.ads.interactivemedia.v3.api.ImaSdkSettings;
import com.tunein.dfpaudiosample.interfaces.IVideoAdListener;
import com.tunein.dfpaudiosample.interfaces.IVideoAdPlayerController;
import com.tunein.dfpaudiosample.interfaces.IVideoAdPlayerView;

/**
 * Ads logic for handling the IMA SDK integration code and events.
 */
public class VideoAdPlayerController implements IVideoAdPlayerController {

    private IVideoAdListener mVideoAdListener;

    /**
     * Container with references to video player and ad UI ViewGroup.
     */
    private AdDisplayContainer mAdDisplayContainer;

    /**
     * The AdsLoader instance exposes the requestAds method.
     */
    private AdsLoader mAdsLoader;

    /**
     * AdsManager exposes methods to control ad playback and listen to ad events.
     */
    private AdsManager mAdsManager;

    /**
     * Factory class for creating SDK objects.
     */
    private ImaSdkFactory mSdkFactory;

    /**
     * Ad-enabled video player.
     */
    private IVideoAdPlayerView mVideoPlayerWithAdPlayback;

    /**
     * VAST ad tag URL to use when requesting ads during video playback.
     */
    private String mCurrentAdTagUrl;

    /**
     * View that we can write log messages to, to display in the UI.
     */
    private Logger mLog;

    public VideoAdPlayerController(Context context, IVideoAdPlayerView player, String language, Logger log) {
        mVideoPlayerWithAdPlayback = player;
        mLog = log;

        // Create an AdsLoader and optionally set the language.
        ImaSdkSettings imaSdkSettings = ImaSdkFactory.getInstance().createImaSdkSettings();
        imaSdkSettings.setLanguage(language);

        mSdkFactory = ImaSdkFactory.getInstance();
        mAdsLoader = mSdkFactory.createAdsLoader(context, imaSdkSettings);

        mAdsLoader.addAdErrorListener(new AdErrorEvent.AdErrorListener() {
            /**
             * An event raised when there is an error loading or playing ads.
             */
            @Override
            public void onAdError(AdErrorEvent event) {
                onImaError(event, "Loader " + event.getError().getMessage());
            }
        });

        mAdsLoader.addAdsLoadedListener(new VideoAdPlayerController.AdsLoadedListener());

        mVideoPlayerWithAdPlayback.setOnContentStateListener(
                new VideoAdPlayerView.OnContentStateListener() {
                    @Override
                    public void onContentStart() {
                        mVideoAdListener.onAdStarted();
                    }

                    /**
                     * Event raised by VideoAdPlayerView when content video is complete.
                     */
                    @Override
                    public void onContentComplete() {
                        mAdsLoader.contentComplete();
                    }
                });
    }

    /**
     * Inner class implementation of AdsLoader.AdsLoaderListener.
     */
    private class AdsLoadedListener implements AdsLoader.AdsLoadedListener {
        /**
         * An event raised when ads are successfully loaded from the ad server via AdsLoader.
         */
        @Override
        public void onAdsManagerLoaded(AdsManagerLoadedEvent adsManagerLoadedEvent) {
            // Ads were successfully loaded, so get the AdsManager instance. AdsManager has
            // events for ad playback and errors.
            mAdsManager = adsManagerLoadedEvent.getAdsManager();

            // Attach event and error event listeners.
            mAdsManager.addAdErrorListener(new AdErrorEvent.AdErrorListener() {
                /**
                 * An event raised when there is an error loading or playing ads.
                 */
                @Override
                public void onAdError(AdErrorEvent event) {
                    onImaError(event, "Manager " + event.getError().getMessage());
                }
            });
            mAdsManager.addAdEventListener(new AdEvent.AdEventListener() {
                /**
                 * Responds to AdEvents.
                 */
                @Override
                public void onAdEvent(AdEvent adEvent) {
                    log("Event: " + adEvent.getType());

                    // These are the suggested event types to handle. For full list of all ad
                    // event types, see the documentation for AdEvent.AdEventType.
                    switch (adEvent.getType()) {
                        case LOADED:
                            mVideoAdListener.onAdLoaded();

                            // AdEventType.LOADED will be fired when ads are ready to be
                            // played. AdsManager.start() begins ad playback. This method is
                            // ignored for VMAP or ad rules playlists, as the SDK will
                            // automatically start executing the playlist.
                            mAdsManager.start();
                            break;
                        case CONTENT_PAUSE_REQUESTED:
                            // AdEventType.CONTENT_PAUSE_REQUESTED is fired immediately before
                            // a video ad is played.
                            pauseContent();
                            break;
                        case CONTENT_RESUME_REQUESTED:
                            // AdEventType.CONTENT_RESUME_REQUESTED is fired when the ad is
                            // completed and you should start playing your content.
                            resumeContent();
                            break;
                        case ALL_ADS_COMPLETED:
                            if (mAdsManager != null) {
                                mAdsManager.destroy();
                                mAdsManager = null;
                            }
                            break;
                        case COMPLETED:
                            // Video ad has completed to the end
                            mVideoAdListener.onAdFinished();
                            break;
                        case CLICKED:
                            mVideoAdListener.onAdClicked();
                            break;
                        default:
                            break;
                    }
                }
            });
            mAdsManager.init();
        }
    }

    private void log(String message) {
        if (mLog != null) {
            mLog.log(message + "\n");
        }
    }

    private void pauseContent() {
        mVideoPlayerWithAdPlayback.pauseContentForAdPlayback();
    }

    private void resumeContent() {
        mVideoAdListener.resumeContent();
    }

    @Override
    public void setAdTagUrl(String adTagUrl) {
        mCurrentAdTagUrl = adTagUrl;
    }

    @Override
    public boolean requestAndPlayAds(IVideoAdListener videoAdListener) {
        mVideoAdListener = videoAdListener;

        // Since we're switching to a new video, tell the SDK the previous video is finished.
        if (mAdsManager != null) {
            mAdsManager.destroy();
        }
        mAdsLoader.contentComplete();

        mAdDisplayContainer = mSdkFactory.createAdDisplayContainer();
        mAdDisplayContainer.setPlayer(mVideoPlayerWithAdPlayback.getVideoAdPlayer());
        mAdDisplayContainer.setAdContainer(mVideoPlayerWithAdPlayback.getAdUiContainer());

        // Create the ads request.
        AdsRequest request = mSdkFactory.createAdsRequest();
        request.setAdTagUrl(mCurrentAdTagUrl);
        request.setAdDisplayContainer(mAdDisplayContainer);
        request.setContentProgressProvider(mVideoPlayerWithAdPlayback.getContentProgressProvider());

        // Request the ad. After the ad is loaded, onAdsManagerLoaded() will be called.
        mAdsLoader.requestAds(request);

        return true;
    }

    @Override
    public void pause() {
        mVideoPlayerWithAdPlayback.savePosition();

        if (mAdsManager != null) {
            mAdsManager.pause();
        } else {
            mVideoPlayerWithAdPlayback.pause();
        }
    }

    @Override
    public void resume() {
        mVideoPlayerWithAdPlayback.restorePosition();
        if (mAdsManager != null) {
            mAdsManager.resume();
        } else {
            mVideoPlayerWithAdPlayback.play();
        }
    }

    @Override
    public void stop() {
        if (mAdsManager != null) {
            mAdsManager.pause();
            mAdsManager.destroy();
        }
    }

    @Override
    public int getCurrentTimeMs() {
        return mVideoPlayerWithAdPlayback.getCurrentTimeMs();
    }

    @Override
    public int getDurationTimeMs() {
        return mVideoPlayerWithAdPlayback.getDurationTimeMs();
    }

    @Override
    public int getBufferedPercentage() {
        return mVideoPlayerWithAdPlayback.getBufferedPercentage();
    }

    private void onImaError(AdErrorEvent event, String message) {
        mVideoAdListener.onAdLoadFailed(message);
        log(message);
        resumeContent();
    }
}
