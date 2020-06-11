/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hoony.musicplayerexample.player

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar

/**
 * SeekBar that can be used with a [MediaSessionCompat] to track and seek in playing
 * media.
 */
class MediaSeekBar : AppCompatSeekBar {
    private var mContext: Context? = null
    private var mMediaController: MediaControllerCompat? = null
    private var mControllerCallback: ControllerCallback? = null
    private var mProgressAnimator: ValueAnimator? = null
    private var mIsTracking = false
    private val mOnSeekBarChangeListener: OnSeekBarChangeListener =
        object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (mContext != null) {
                    // TODO : 재생 화면의 현재 재생시간 업데이트
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                mIsTracking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                mMediaController!!.transportControls.seekTo(progress.toLong())
                mIsTracking = false
            }
        }

    constructor(context: Context?) : super(context) {
        super.setOnSeekBarChangeListener(mOnSeekBarChangeListener)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        super.setOnSeekBarChangeListener(mOnSeekBarChangeListener)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        super.setOnSeekBarChangeListener(mOnSeekBarChangeListener)
    }

    override fun setOnSeekBarChangeListener(l: OnSeekBarChangeListener) { // Prohibit adding seek listeners to this subclass.
        throw UnsupportedOperationException("Cannot add listeners to a MediaSeekBar")
    }

    fun setMediaController(context: Context?, mediaController: MediaControllerCompat?) {
        mContext = context
        if (mediaController != null) {
            mControllerCallback = ControllerCallback()
            mediaController.registerCallback(mControllerCallback!!)
        } else if (mMediaController != null) {
            mMediaController!!.unregisterCallback(mControllerCallback!!)
            mControllerCallback = null
        }
        mMediaController = mediaController
    }

    // 재생중 화면의 Seek bar update
    fun updateProgress() {
        val state = if (mMediaController != null) mMediaController!!.playbackState else null
        val metadata = if (mMediaController != null) mMediaController!!.metadata else null
        if (state != null && metadata != null) {
            max = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION).toInt()
            progress = state.position.toInt()
            mControllerCallback!!.startProgressAnimator(state)
        }
    }

    fun disconnectController() {
        if (mMediaController != null) {
            mMediaController!!.unregisterCallback(mControllerCallback!!)
            mControllerCallback = null
            mMediaController = null
        }
    }

    private inner class ControllerCallback : MediaControllerCompat.Callback(),
        AnimatorUpdateListener {
        override fun onSessionDestroyed() {
            super.onSessionDestroyed()
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
            super.onPlaybackStateChanged(state)
            // If there's an ongoing animation, stop it now.
            if (mProgressAnimator != null) {
                mProgressAnimator!!.cancel()
                mProgressAnimator = null
            }
            val progress = state.position.toInt() ?: 0
            setProgress(progress)
            // If the media is playing then the seekbar should follow it, and the easiest
            // way to do that is to create a ValueAnimator to update it so the bar reaches0
            // the end of the media the same time as playback gets there (or close enough).
            if (state.state == PlaybackStateCompat.STATE_PLAYING) {
                val timeToEnd = ((max - progress) / state.playbackSpeed).toInt()
                mProgressAnimator = ValueAnimator.ofInt(progress, max)
                    .setDuration(timeToEnd.toLong())
                mProgressAnimator?.let {
                    it.interpolator = LinearInterpolator()
                    it.addUpdateListener(this)
                    it.start()
                }
            }
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat) {
            super.onMetadataChanged(metadata)
            val max = metadata?.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)?.toInt() ?: 0
            progress = 0
            setMax(max)
        }

        override fun onAnimationUpdate(valueAnimator: ValueAnimator) { // If the user is changing the slider, cancel the animation.
            if (mIsTracking) {
                valueAnimator.cancel()
                return
            }
            val animatedIntValue = valueAnimator.animatedValue as Int
            progress = animatedIntValue
        }

        fun startProgressAnimator(state: PlaybackStateCompat?) {
            val progress = state?.position?.toInt() ?: 0
            setProgress(progress)
            // If the media is playing then the seekbar should follow it, and the easiest
            // way to do that is to create a ValueAnimator to update it so the bar reaches0
            // the end of the media the same time as playback gets there (or close enough).
            if (state != null && state.state == PlaybackStateCompat.STATE_PLAYING) {
                val timeToEnd = ((max - progress) / state.playbackSpeed).toInt()
                mProgressAnimator = ValueAnimator.ofInt(progress, max)
                    .setDuration(timeToEnd.toLong())
                mProgressAnimator?.let {
                    it.interpolator = LinearInterpolator()
                    it.addUpdateListener(this)
                    it.start()
                }
            }
        }
    }
}