package com.hoony.musicplayerexample.player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager

abstract class PlayerAdapter {
    companion object {
        private val MEDIA_VOLUME_DEFAULT: Float = 1.0f
        private val MEDIA_VOLUME_DUCK: Float = 0.2f

        private val AUDIO_NOISY_INTENT_FILTER: IntentFilter =
            IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    }

    private var mAudioNoisyReceiverRegistered: Boolean = false
    private val mAudioNoisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY == intent?.action) {

            }
        }
    }
}