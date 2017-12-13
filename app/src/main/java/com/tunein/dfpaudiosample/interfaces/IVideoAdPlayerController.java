package com.tunein.dfpaudiosample.interfaces;


/**
 * Internal interface for Video Player controller that is responsible for video ad loading and
 * video playback states controls.
 */
public interface IVideoAdPlayerController {

    /**
     * Set the ad tag URL the player should use to request ads when playing a content video.
     */
    void setAdTagUrl(String adTagUrl);

    /**
     * Save position of the video, whether content or ad. Can be called when the app is paused, for example.
     */
    void pause();

    /**
     * Restore the previously saved progress location of the video. Can be called when the app is resumed.
     */
    void resume();

    /**
     * User requested stop for an ad.
     */
    void stop();

    /**
     * Request and subsequently play video ads from the ad server.
     *
     * @return {@code true} if Ad was successfully requested
     */
    boolean requestAndPlayAds(IVideoAdListener videoAdListener);

    /**
     * Returns the current time of the video Ad in seconds.
     */
    int getCurrentTimeMs();

    /**
     * Returns the duration time of the video Ad in seconds.
     */
    int getDurationTimeMs();

    /**
     * Returns amount of buffered content in percents.
     */
    int getBufferedPercentage();

    interface Logger {
        void log(String logMessage);
    }
}
