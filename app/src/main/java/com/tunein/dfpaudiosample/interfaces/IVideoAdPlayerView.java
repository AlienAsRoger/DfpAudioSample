package com.tunein.dfpaudiosample.interfaces;

import android.view.ViewGroup;

import com.google.ads.interactivemedia.v3.api.player.ContentProgressProvider;
import com.google.ads.interactivemedia.v3.api.player.VideoAdPlayer;
import com.tunein.dfpaudiosample.videoplayer.VideoAdPlayerView;


/**
 * Interface for communication between IMA SDK and video player with Ad playback
 */
public interface IVideoAdPlayerView {

    void setOnContentStateListener(VideoAdPlayerView.OnContentStateListener onContentStateListener);

    void pauseContentForAdPlayback();

    VideoAdPlayer getVideoAdPlayer();

    ViewGroup getAdUiContainer();

    ContentProgressProvider getContentProgressProvider();

    void savePosition();

    void play();

    void pause();

    void restorePosition();

    int getCurrentTimeMs();

    int getDurationTimeMs();

    int getBufferedPercentage();
}
