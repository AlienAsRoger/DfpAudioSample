package com.tunein.dfpaudiosample.inject;

import android.content.Context;
import android.util.Log;
import android.view.View;


import com.tunein.dfpaudiosample.R;
import com.tunein.dfpaudiosample.inject.scopes.VideoAdScope;
import com.tunein.dfpaudiosample.interfaces.IVideoAdPlayerController;
import com.tunein.dfpaudiosample.interfaces.IVideoAdPlayerView;
import com.tunein.dfpaudiosample.videoplayer.VideoAdPlayerController;
import com.tunein.dfpaudiosample.videoplayer.VideoAdPlayerView;

import dagger.Module;
import dagger.Provides;


/**
 * Module that provides entities for VideoAd related classes. This module lives as long as {@link VideoAdScope} and
 * {@link com.tunein.dfpaudiosample.inject.components.VideoAdComponent} lives
 */
@Module
public class VideoAdModule {

    private final Context mContext;

    public VideoAdModule(Context context) {
        mContext = context;
    }

    @Provides
    @VideoAdScope
    IVideoAdPlayerController.Logger provideVideoAdLogger() {
        return new VideoAdPlayerController.Logger() {
            @Override
            public void log(String message) {
                Log.d("TEST", "log() called with: message = [" + message + "]");
            }
        };
    }

    @Provides
    @VideoAdScope
    IVideoAdPlayerView provideVideoPlayerWithAdPlayback() {
        return (VideoAdPlayerView) View.inflate(mContext, R.layout.video_player_layout, null);
    }

    @Provides
    @VideoAdScope
    IVideoAdPlayerController provideVideoAdPlayerController(IVideoAdPlayerView player, IVideoAdPlayerController.Logger logger) {
        return new VideoAdPlayerController(mContext, player, "en", logger);
    }

}
