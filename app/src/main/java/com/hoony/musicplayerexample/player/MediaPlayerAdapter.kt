package com.hoony.musicplayerexample.player

import android.content.Context
import android.os.SystemClock
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.audio.AudioListener
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.video.VideoListener

class MediaPlayerAdapter(
    context: Context,
    private val listener: PlaybackInfoListener
) : PlayerAdapter(context) {

    private var state: Int = -1
    private var currentMediaPlayedToCompletion: Boolean = false

    // Work-around for a MediaPlayer bug related to the behavior of MediaPlayer.seekTo()
    // while not playing.
    private var seekWhileNotPlaying: Int = -1

    private val exoPlayer = ExoPlayerFactory.newSimpleInstance(context)

    init {
        exoPlayer.addAudioListener(object : AudioListener {
            override fun onAudioAttributesChanged(audioAttributes: AudioAttributes?) {
                super.onAudioAttributesChanged(audioAttributes)
            }

            override fun onVolumeChanged(volume: Float) {
                super.onVolumeChanged(volume)
            }

            override fun onAudioSessionId(audioSessionId: Int) {
                super.onAudioSessionId(audioSessionId)
            }
        })
        exoPlayer.addVideoListener(object : VideoListener {
            override fun onVideoSizeChanged(
                width: Int,
                height: Int,
                unappliedRotationDegrees: Int,
                pixelWidthHeightRatio: Float
            ) {
                super.onVideoSizeChanged(
                    width,
                    height,
                    unappliedRotationDegrees,
                    pixelWidthHeightRatio
                )
            }

            override fun onRenderedFirstFrame() {
                super.onRenderedFirstFrame()
            }

            override fun onSurfaceSizeChanged(width: Int, height: Int) {
                super.onSurfaceSizeChanged(width, height)
            }
        })
        exoPlayer.addListener(object : Player.EventListener {
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
                super.onPlaybackParametersChanged(playbackParameters)
            }

            override fun onSeekProcessed() {
                super.onSeekProcessed()
            }

            override fun onTracksChanged(
                trackGroups: TrackGroupArray?,
                trackSelections: TrackSelectionArray?
            ) {
                super.onTracksChanged(trackGroups, trackSelections)
            }

            override fun onPlayerError(error: ExoPlaybackException?) {
                super.onPlayerError(error)
            }

            override fun onLoadingChanged(isLoading: Boolean) {
                super.onLoadingChanged(isLoading)
            }

            override fun onPositionDiscontinuity(reason: Int) {
                super.onPositionDiscontinuity(reason)
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                super.onRepeatModeChanged(repeatMode)
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                super.onShuffleModeEnabledChanged(shuffleModeEnabled)
            }

            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
                super.onTimelineChanged(timeline, manifest, reason)
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                super.onPlayerStateChanged(playWhenReady, playbackState)
            }
        })
    }

    override fun playFromMedia(metaData: MediaMetadataCompat?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCurrentMedia(): MediaMetadataCompat {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isPlaying(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPlay() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPause() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    // This is the main reducer for the player state machine.
    private fun setNewState(newPlayState: Int) {
        state = newPlayState

        // Whether playback goes to completion, or whether it is stopped, the
        // mCurrentMediaPlayedToCompletion is set to true.
        if (state == PlaybackStateCompat.STATE_STOPPED) {
            currentMediaPlayedToCompletion = true
        }

        // Work around for MediaPlayer.getCurrentPosition() when it changes while not playing.
        val reportPosition: Long = if (seekWhileNotPlaying >= 0) {
            seekWhileNotPlaying.toLong()
        } else {
            exoPlayer.currentPosition
        }

        val stateBuilder = PlaybackStateCompat.Builder()
        stateBuilder.setActions()
        stateBuilder.setState(
            this.state,
            reportPosition,
            1.0f,
            SystemClock.elapsedRealtime())
            this.listener.onPlaybackStateChange(stateBuilder.build())
        )
    }

    override fun onStop() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun seekTo(position: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setVolume(volume: Float) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}