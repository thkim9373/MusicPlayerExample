package com.hoony.musicplayerexample.player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaMetadata
import android.support.v4.media.MediaMetadataCompat

abstract class PlayerAdapter(private val mApplicationContext: Context) {

    companion object {
        private const val MEDIA_VOLUME_DEFAULT: Float = 1.0f
        private const val MEDIA_VOLUME_DUCK: Float = 0.2f

        private val AUDIO_NOISY_INTENT_FILTER: IntentFilter =
            IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    }

    private val mAudioManager: AudioManager =
        mApplicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val mAudioFocusHelper: AudioFocusHelper = AudioFocusHelper()

    private var mAudioNoisyReceiverRegistered: Boolean = false
    private val mAudioNoisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY == intent?.action) {
                if (isPlaying()) {
                    pause()
                }
            }
        }
    }

    private var mPlayOnAudioFocus: Boolean = false

    abstract fun playFromMedia(metaData: MediaMetadataCompat?)

    abstract fun getCurrentMedia(): MediaMetadataCompat

    abstract fun isPlaying(): Boolean

    fun play() {
        if (mAudioFocusHelper.requestAudioFocus()) {
            registerAudioNoisyReceiver()
            onPlay()
        }
    }

    abstract fun onPlay()

    fun pause() {
        if (!mPlayOnAudioFocus) {
            mAudioFocusHelper.abandonAudioFocus()
        }

        unregisterAudioNoisyReceiver()
        onPause()
    }

    abstract fun onPause()

    fun stop() {
        mAudioFocusHelper.abandonAudioFocus()
        unregisterAudioNoisyReceiver()
        onStop()
    }

    abstract fun onStop()

    abstract fun seekTo(position: Long)

    abstract fun setVolume(volume: Float)

    private fun registerAudioNoisyReceiver() {
        if (!mAudioNoisyReceiverRegistered) {
            mApplicationContext.registerReceiver(mAudioNoisyReceiver, AUDIO_NOISY_INTENT_FILTER)
            mAudioNoisyReceiverRegistered = true
        }
    }

    private fun unregisterAudioNoisyReceiver() {
        if (!mAudioNoisyReceiverRegistered) {
            mApplicationContext.unregisterReceiver(mAudioNoisyReceiver)
            mAudioNoisyReceiverRegistered = false
        }
    }

    private inner class AudioFocusHelper : AudioManager.OnAudioFocusChangeListener {

        fun requestAudioFocus(): Boolean {
            val result = mAudioManager.requestAudioFocus(
                this,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
            return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }

        fun abandonAudioFocus() {
            mAudioManager.abandonAudioFocus(this)
        }

        override fun onAudioFocusChange(focusChange: Int) {
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> {
                    if (mPlayOnAudioFocus && !isPlaying()) {
                        play()
                    } else if (isPlaying()) {
                        setVolume(MEDIA_VOLUME_DEFAULT)
                    }
                    mPlayOnAudioFocus = false
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    setVolume(MEDIA_VOLUME_DUCK)
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    if (isPlaying()) {
                        mPlayOnAudioFocus = true
                        pause()
                    }
                }
                AudioManager.AUDIOFOCUS_LOSS -> {
                    mAudioManager.abandonAudioFocus(this)
                    mPlayOnAudioFocus = false
                    stop()
                }
            }
        }
    }
}