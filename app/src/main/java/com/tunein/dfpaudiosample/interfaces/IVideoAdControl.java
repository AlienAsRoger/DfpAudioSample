package com.tunein.dfpaudiosample.interfaces;

/**
 * Interface to control Video Ads
 */
public interface IVideoAdControl extends IAdControl {

    void onPlayClicked();

    void onPauseClicked();

    void onStopClicked();

    int getVideoPositionMs();

    int getVideoDurationMs();

    int getVideoBufferPercentage();
}
