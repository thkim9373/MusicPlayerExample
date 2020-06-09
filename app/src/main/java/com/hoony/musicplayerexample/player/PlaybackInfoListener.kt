package com.hoony.musicplayerexample.player

import android.support.v4.media.session.PlaybackStateCompat

/**
 * Listener to provide state updates from {@see MediaPlayerAdapter} (the media player)
 * to {@link MusicService} (the service that holds our {@link MediaSessionCompat}.
 */
abstract class PlaybackInfoListener {
    abstract fun onPlaybackStateChange(state: PlaybackStateCompat)
}