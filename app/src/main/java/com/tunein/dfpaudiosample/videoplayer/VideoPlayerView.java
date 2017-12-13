// Copyright 2014 Google Inc. All Rights Reserved.

package com.tunein.dfpaudiosample.videoplayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.AttributeSet;
import android.widget.MediaController;
import android.widget.VideoView;

import com.tunein.dfpaudiosample.interfaces.IVideoPlayer;

import java.util.ArrayList;
import java.util.List;


/**
 * A VideoView that intercepts various methods and reports them back via a IPlayerCallback.
 */
public class VideoPlayerView extends VideoView implements IVideoPlayer, OnPreparedListener, OnCompletionListener,
        OnErrorListener {

    private enum PlaybackState {
        STOPPED,
        PAUSED,
        PLAYING
    }

    private MediaController mMediaController;
    private PlaybackState mPlaybackState;
    private final List<IPlayerCallback> mVideoPlayerCallbacks = new ArrayList<IPlayerCallback>(1);

    public VideoPlayerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public VideoPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoPlayerView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mPlaybackState = PlaybackState.STOPPED;
        mMediaController = new MediaController(getContext());
        mMediaController.setAnchorView(this);
        enablePlaybackControls();

        // hide black overlay
        setAlpha(0f);

        // Set OnPrepareListener and OnInfo to update view parameters when ready.
        // It's to avoid showing black screen while loading
        setOnPreparedListener(this);

        // Set OnCompletionListener to notify our callbacks when the video is completed.
        super.setOnCompletionListener(this);

        // Set OnErrorListener to notify our callbacks if the video errors.
        super.setOnErrorListener(this);
    }

    @Override
    public int getVideoDurationMs() {
        return mPlaybackState == PlaybackState.STOPPED ? 0 : super.getDuration();
    }

    @Override
    public int getBufferedPercentage() {
        return getBufferPercentage();
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        // The OnCompletionListener can only be implemented by VideoPlayerView.
        throw new UnsupportedOperationException();
    }

    @Override
    public void setOnErrorListener(OnErrorListener listener) {
        // The OnErrorListener can only be implemented by VideoPlayerView.
        throw new UnsupportedOperationException();
    }

    @Override
    public void play() {
        start();
    }

    @Override
    public void start() {
        super.start();
        switch (mPlaybackState) {
            case STOPPED:
                for (IPlayerCallback callback : mVideoPlayerCallbacks) {
                    callback.onPlay();
                }
                break;
            case PAUSED:
                for (IPlayerCallback callback : mVideoPlayerCallbacks) {
                    callback.onResume();
                }
                break;
            default:
                // Already playing; do nothing.
                break;
        }
        mPlaybackState = PlaybackState.PLAYING;
    }

    @Override
    public void pause() {
        super.pause();
        mPlaybackState = PlaybackState.PAUSED;
        for (IPlayerCallback callback : mVideoPlayerCallbacks) {
            callback.onPause();
        }
    }

    @Override
    public int getCurrentPositionMs() {
        return getCurrentPosition();
    }

    @Override
    public void stopPlayback() {
        if (mPlaybackState == PlaybackState.STOPPED) {
            return;
        }
        super.stopPlayback();
        mPlaybackState = PlaybackState.STOPPED;
    }

    @Override
    public void disablePlaybackControls() {
        setMediaController(null);
    }

    @Override
    public void enablePlaybackControls() {
        setMediaController(mMediaController);
    }

    @Override
    public void addPlayerCallback(IPlayerCallback callback) {
        mVideoPlayerCallbacks.add(callback);
    }

    @Override
    public void removePlayerCallback(IPlayerCallback callback) {
        mVideoPlayerCallbacks.remove(callback);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    for (IPlayerCallback callback : mVideoPlayerCallbacks) {
                        callback.onStart();
                    }

                    setAlpha(1f); // shows view
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        // Reset the MediaPlayer.
        // This prevents a race condition which occasionally results in the media
        // player crashing when switching between videos.
        disablePlaybackControls();
        mediaPlayer.reset();
        mediaPlayer.setDisplay(getHolder());
        enablePlaybackControls();
        mPlaybackState = PlaybackState.STOPPED;

        for (IPlayerCallback callback : mVideoPlayerCallbacks) {
            callback.onCompleted();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        mPlaybackState = PlaybackState.STOPPED;
        for (IPlayerCallback callback : mVideoPlayerCallbacks) {
            callback.onError();
        }

        // Returning true signals to MediaPlayer that we handled the error. This will
        // prevent the completion handler from being called.
        return true;
    }
}
