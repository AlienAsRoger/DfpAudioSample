package com.tunein.dfpaudiosample.adapters;


import android.util.Log;
import android.view.ViewGroup;

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

//    private static final String VAST_TAG = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/"
//            + "single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast"
//            + "&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct"
//            + "%3Dlinear&correlator=";

    private static final String VAST_TAG = "https://pubads.g.doubleclick.net/gampad/ads?iu=/15480783/Mobile-Preroll-Video/Android&correlator=1525723092666&env=vp&impl=s&url=tunein.player&gdfp_req=1&output=vast&unviewed_position_start=1&ciu_szs=300x250&sz=1x1%7C400x300%7C640x360%7C640x480&cust_params=useragent%3DDev+-+TuneIn+Radio%2F19.9+%28Android+27%3B+Nexus+5X%3B+Java%29%26partnerId%3DxwhZkVKi%26ListingId%3Ds249937%26genre_id%3Dg2804%26class%3Dmusic%26stationId%3Ds249937%26is_mature%3Dfalse%26is_family%3Dfalse%26is_event%3Dfalse%26is_ondemand%3Dfalse%26language%3Den_US%26version%3D19.9%26persona%3DMusic%26is_new_user%3Dfalse%26device%3Dphone%26country_region_id%3D100436%26videoEnabled%3Dfalse%26audioEnabled%3Dtrue%26station_language%3DEnglish%26abtest%3D99899%26lotamesegments%3Dall%2CTCM%2CGlu%2Cpod%2CHyattQ1_2108_BT%2CDelta%2CFiosSMB%2CUSNavy2018%2CComcastSMBQ2%2CLAPhil%2CINS%2C711Rewards%2CLAPhil%2CNAREIT_BT%2CStateFarmNFL%2CMillerLite%2CCanadaDry%2CUSN2%2CRoyalCarib_BT%2CVerizonJP%2CVZ_Altice%2CChoiceHotels%2CLexus";

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
    public ViewGroup getCompanionAdView() {
        return mListener.getCompanionAdView();
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
