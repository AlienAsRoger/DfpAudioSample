// Copyright 2014 Google Inc. All Rights Reserved.

package com.tunein.dfpaudiosample.videoplayer;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.google.ads.interactivemedia.v3.api.player.ContentProgressProvider;
import com.google.ads.interactivemedia.v3.api.player.VideoAdPlayer;
import com.google.ads.interactivemedia.v3.api.player.VideoProgressUpdate;
import com.tunein.dfpaudiosample.R;
import com.tunein.dfpaudiosample.interfaces.IVideoAdPlayerView;
import com.tunein.dfpaudiosample.interfaces.IVideoPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Video player that can play content video and ads.
 */
public class VideoAdPlayerView extends ConstraintLayout implements IVideoAdPlayerView {

    /**
     * Interface for alerting caller of video state (complete/start).
     */
    public interface OnContentStateListener {

        void onContentStart();

        void onContentComplete();
    }

    /**
     * The wrapped video player.
     */
    private IVideoPlayer mVideoPlayer;

    /**
     * The SDK will render ad playback UI elements into this ViewGroup.
     */
    private ViewGroup mAdUiContainer;

    /**
     * The saved position in the ad to resume if app is backgrounded during ad playback.
     */
    private int mSavedAdPosition;

    /**
     * Called when the content is completed.
     */
    private OnContentStateListener mOnContentStateListener;

    /**
     * VideoAdPlayer interface implementation for the SDK to send ad play/pause type events.
     */
    private VideoAdPlayer mVideoAdPlayer;

    /**
     * ContentProgressProvider interface implementation for the SDK to check content progress.
     */
    private ContentProgressProvider mContentProgressProvider;

    private final List<VideoAdPlayer.VideoAdPlayerCallback> mAdCallbacks = new ArrayList<>(1);

    public VideoAdPlayerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VideoAdPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoAdPlayerView(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init() {
        mSavedAdPosition = 0;
        mVideoPlayer = (IVideoPlayer) getRootView().findViewById(R.id.videoPlayer);
        mAdUiContainer = (ViewGroup) getRootView().findViewById(R.id.adUiContainer);

        // Define VideoAdPlayer connector.
        mVideoAdPlayer = new VideoAdPlayer() {
            @Override
            public void playAd() {
                mVideoPlayer.play();
            }

            @Override
            public void loadAd(String url) {
                mVideoPlayer.setVideoPath(url);
            }

            @Override
            public void stopAd() {
                mVideoPlayer.stopPlayback();
            }

            @Override
            public void pauseAd() {
                mVideoPlayer.pause();
            }

            @Override
            public void resumeAd() {
                playAd();
            }

            @Override
            public void addCallback(VideoAdPlayerCallback videoAdPlayerCallback) {
                mAdCallbacks.add(videoAdPlayerCallback);
            }

            @Override
            public void removeCallback(VideoAdPlayerCallback videoAdPlayerCallback) {
                mAdCallbacks.remove(videoAdPlayerCallback);
            }

            @Override
            public VideoProgressUpdate getAdProgress() {
                if (mVideoPlayer.getVideoDurationMs() <= 0) {
                    return VideoProgressUpdate.VIDEO_TIME_NOT_READY;
                }
                return new VideoProgressUpdate(mVideoPlayer.getCurrentPositionMs(),
                        mVideoPlayer.getVideoDurationMs());
            }
        };

        mContentProgressProvider = new ContentProgressProvider() {
            @Override
            public VideoProgressUpdate getContentProgress() {
                if (mVideoPlayer.getVideoDurationMs() <= 0) {
                    return VideoProgressUpdate.VIDEO_TIME_NOT_READY;
                }
                return new VideoProgressUpdate(mVideoPlayer.getCurrentPositionMs(), mVideoPlayer.getVideoDurationMs());
            }
        };

        // Set player callbacks for delegating major video events.
        mVideoPlayer.addPlayerCallback(new IVideoPlayer.IPlayerCallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onPlay() {
                for (VideoAdPlayer.VideoAdPlayerCallback callback : mAdCallbacks) {
                    callback.onPlay();
                }
                if (mOnContentStateListener != null) {
                    mOnContentStateListener.onContentStart();
                }
            }

            @Override
            public void onPause() {
                for (VideoAdPlayer.VideoAdPlayerCallback callback : mAdCallbacks) {
                    callback.onPause();
                }
            }

            @Override
            public void onResume() {
                for (VideoAdPlayer.VideoAdPlayerCallback callback : mAdCallbacks) {
                    callback.onResume();
                }
            }

            @Override
            public void onError() {
                for (VideoAdPlayer.VideoAdPlayerCallback callback : mAdCallbacks) {
                    callback.onError();
                }
            }

            @Override
            public void onCompleted() {
                for (VideoAdPlayer.VideoAdPlayerCallback callback : mAdCallbacks) {
                    callback.onEnded();
                }
            }
        });
    }

    /**
     * Set a listener to be triggered when the content (non-ad) video completes.
     */
    public void setOnContentStateListener(OnContentStateListener listener) {
        mOnContentStateListener = listener;
    }

    /**
     * Save the playback progress state of the currently playing video. This is called when content
     * is paused to prepare for ad playback or when app is backgrounded.
     */
    @Override
    public void savePosition() {
        mSavedAdPosition = mVideoPlayer.getCurrentPositionMs();
    }

    /**
     * Restore the currently loaded video to its previously saved playback progress state. This is
     * called when content is resumed after ad playback or when focus has returned to the app.
     */
    @Override
    public void restorePosition() {
        mVideoPlayer.seekTo(mSavedAdPosition);
    }

    /**
     * Pauses the content video.
     */
    @Override
    public void pause() {
        mVideoPlayer.pause();
    }

    /**
     * Plays the content video.
     */
    @Override
    public void play() {
        mVideoPlayer.play();
    }

    /**
     * Returns current video ad play time in ms.
     */
    @Override
    public int getCurrentTimeMs() {
        return mVideoPlayer.getCurrentPositionMs();
    }

    /**
     * Returns video play duration time in ms.
     */
    @Override
    public int getDurationTimeMs() {
        return mVideoPlayer.getVideoDurationMs();
    }

    @Override
    public int getBufferedPercentage() {
        return mVideoPlayer.getBufferedPercentage();
    }

    /**
     * Pause the currently playing content video in preparation for an ad to play, and disables
     * the media controller.
     */
    @Override
    public void pauseContentForAdPlayback() {
        mVideoPlayer.disablePlaybackControls();
        savePosition();
        mVideoPlayer.stopPlayback();
    }

    /**
     * Returns the UI element for rendering video ad elements.
     */
    @Override
    public ViewGroup getAdUiContainer() {
        return mAdUiContainer;
    }

    /**
     * Returns an implementation of the SDK's VideoAdPlayer interface.
     */
    @Override
    public VideoAdPlayer getVideoAdPlayer() {
        return mVideoAdPlayer;
    }

    @Override
    public ContentProgressProvider getContentProgressProvider() {
        return mContentProgressProvider;
    }
}
