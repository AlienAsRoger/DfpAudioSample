package com.tunein.dfpaudiosample.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tunein.dfpaudiosample.R;
import com.tunein.dfpaudiosample.ads.AdHelper;
import com.tunein.dfpaudiosample.inject.VideoAdModule;
import com.tunein.dfpaudiosample.inject.components.AppComponent;
import com.tunein.dfpaudiosample.inject.components.SampleAppComponent;
import com.tunein.dfpaudiosample.inject.components.VideoAdComponent;
import com.tunein.dfpaudiosample.interfaces.IInjectableFactory;
import com.tunein.dfpaudiosample.interfaces.IVideoAdControl;
import com.tunein.dfpaudiosample.interfaces.VideoAdResponseListener;
import com.tunein.dfpaudiosample.utils.TimeToStringConverter;

import javax.inject.Inject;

/**
 * A placeholder fragment containing a simple view.
 */
public class VideoPrerollFragment extends BaseFragment implements VideoAdResponseListener, View.OnClickListener {

//    private String mVideoPrerollAdUnitId = "/15480783/Mobile-Preroll-Video-Stage/Android-Preroll-Video-Stage";
    private String mVideoPrerollAdUnitId = "/124319096/external/single_ad_samples";
    private View mCompanionAdView;
//    private String mVideoPrerollAdUnitId = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/"
//            + "single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast"
//            + "&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct"
//            + "%3Dlinear&correlator=";

    private enum VideoStates {
        PLAYING, PAUSED
    }

    private FrameLayout mContainerView;

    @Inject
    IInjectableFactory mInjectableFactory;

    private AdHelper mAdHelper;

    private VideoAdComponent mVideoAdComponent;
    private Button mStopButton;
    private Button mPlayButton;
    private ProgressBar mLoadingProgressBar;
    private ProgressBar mProgressBar;
    private TextView mCurrentTimeTxt;
    private TextView mEndTimeTxt;
    private VideoStates mVideoState;
    private IVideoAdControl mAdControl;
    private Handler mHandler;
    private TimeToStringConverter mStringConverter;

    public VideoPrerollFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mVideoAdComponent = getAppComponent().plus(new VideoAdModule(getActivity()));
        mAdHelper = mInjectableFactory.createAdHelper(mVideoAdComponent);
        mHandler = new Handler();
        mStringConverter = new TimeToStringConverter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_preroll, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initWidgets(view);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mAdControl == null) {
            if (TextUtils.isEmpty(mVideoPrerollAdUnitId)) {
                return;
            }

            mLoadingProgressBar.setVisibility(View.VISIBLE);
            mAdControl = (IVideoAdControl) mAdHelper.requestAd(AdHelper.NOW_PLAYING, this);
        } else {
            onPlayClick();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        mHandler.removeCallbacks(mProgressUpdateRunnable);
        onPauseClick();
    }

    @Override
    public void onAdLoaded() {
        Log.d("TEST", "onAdLoaded: ");
    }

    @Override
    public void onAdLoadFailed(String message) {
        Log.d("TEST", "onAdLoadFailed: " + message);
    }

    @Override
    public void addAdViewToContainer(Object adView) {
        if (adView == null) {
            return;
        }
        // remove all existing views in layout
        mContainerView.removeAllViews();
        ViewGroup parent = (ViewGroup) ((View) adView).getParent();
        if (parent != null) {
            parent.removeAllViews();
        }
        mContainerView.addView((View) adView);
    }

    @Override
    public String getVideoSupportedSizes() {
        return "1x1|640x480|640x360";
    }

    @Override
    public ViewGroup getCompanionAdView() {
        return (ViewGroup) mCompanionAdView;
    }

    @Override
    public String getAdUnitId() {
        return mVideoPrerollAdUnitId;
    }

    public void onPlayClick() {
        if (mAdControl == null) {
            return;
        }
        mVideoState = VideoStates.PLAYING;
        mPlayButton.setText(R.string.pause);

        mAdControl.onPlayClicked();
    }

    public void onPauseClick() {
        if (mAdControl == null) {
            return;
        }
        mVideoState = VideoStates.PAUSED;
        mPlayButton.setText(R.string.play);

        mAdControl.onPauseClicked();
    }

    private void onStopClick() {
        mAdControl.onStopClicked();
    }

    @Override
    public void onVideoAdStarted() {
        mVideoState = VideoStates.PLAYING;
        mPlayButton.setText(R.string.pause);

        mLoadingProgressBar.setVisibility(View.GONE);

        mHandler.postDelayed(mProgressUpdateRunnable, 1000);
    }

    @Override
    public void resumeContent() {
        mHandler.removeCallbacks(mProgressUpdateRunnable);

        View view = getView();
        if (view == null) {
            return;
        }
        Snackbar.make(view, "Resume content", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onVideoAdFinished() {
        mHandler.removeCallbacks(mProgressUpdateRunnable);
        mProgressBar.setProgress(1000);

        View view = getView();
        if (view == null) {
            return;
        }
        Snackbar.make(view, "AdFinished", Snackbar.LENGTH_LONG).show();
    }

    private int setProgress() {
        int position = mAdControl.getVideoPositionMs();
        int duration = mAdControl.getVideoDurationMs();
        if (duration > 0) {
            float pos = (1000L * position) / duration;
            mProgressBar.setProgress((int) pos);
        }

        int percent = mAdControl.getVideoBufferPercentage();
        mProgressBar.setSecondaryProgress(percent * 10);

        mCurrentTimeTxt.setText(mStringConverter.stringForTime(position));
        String timeLeft = mStringConverter.stringForTime(duration - position);
        mEndTimeTxt.setText(getContext().getString(R.string.minus_symbol_arg, timeLeft));

        return position;
    }

    private void initWidgets(View view) {
        mContainerView = view.findViewById(R.id.video_container);
        mCompanionAdView = view.findViewById(R.id.companion_ad_view);
        mStopButton = view.findViewById(R.id.stop_button);
        mPlayButton = view.findViewById(R.id.play_button);
        mLoadingProgressBar = view.findViewById(R.id.loading_progress_bar);
        mProgressBar = view.findViewById(R.id.progress_bar);
        mCurrentTimeTxt = view.findViewById(R.id.current_time);
        mEndTimeTxt = view.findViewById(R.id.end_time);

        mStopButton.setOnClickListener(this);
        mPlayButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.stop_button) {
            onStopClick();
        } else if (v.getId() == R.id.play_button) {
            if (mVideoState == VideoStates.PAUSED) {
                onPlayClick();
            } else {
                onPauseClick();
            }
        }
    }

    private Runnable mProgressUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.removeCallbacks(this);

            int pos = setProgress();
            if (mAdControl.getVideoPositionMs() > 0) {
                mHandler.postDelayed(mProgressUpdateRunnable, 1000 - (pos % 1000));
            } else {
                mHandler.postDelayed(this, 1000);
            }
        }
    };

    // ----- Dependency injection ------

    @Override
    protected void inject(AppComponent appComponent) {
        ((SampleAppComponent) appComponent).inject(this);
    }

}
