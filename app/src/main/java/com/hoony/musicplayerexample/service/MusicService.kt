package com.hoony.musicplayerexample.service

import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import com.hoony.musicplayerexample.player.MediaNotificationManager
import com.hoony.musicplayerexample.player.MediaPlayerAdapter
import com.hoony.musicplayerexample.player.MusicLibrary.getMetadata
import com.hoony.musicplayerexample.player.MusicLibrary.mediaItems
import com.hoony.musicplayerexample.player.MusicLibrary.root
import com.hoony.musicplayerexample.player.PlaybackInfoListener
import com.hoony.musicplayerexample.player.PlayerAdapter
import java.util.*

class MusicService : MediaBrowserServiceCompat() {

    private var mMediaSession: MediaSessionCompat? = null
    private var mPlayback: PlayerAdapter? = null
    private var mMediaNotificationManager: MediaNotificationManager? = null
    private var mCallback: MediaSessionCallback? = null
    private var mServiceInStartedState = false

    override fun onCreate() {
        super.onCreate()
        // Create a new MediaSession.
        mMediaSession = MediaSessionCompat(this, "MusicService")
        mCallback = MediaSessionCallback()
        mMediaSession!!.setCallback(mCallback)
        mMediaSession!!.setFlags(
            MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                    MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS or
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        )
        sessionToken = mMediaSession!!.sessionToken
        mMediaNotificationManager = MediaNotificationManager(this)
        mPlayback = MediaPlayerAdapter(this, MediaPlayerListener())
        Log.d(TAG, "onCreate: MusicService creating MediaSession, and MediaNotificationManager")
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        //        stopSelf();
    }

    override fun onDestroy() {
        mMediaNotificationManager!!.onDestroy()
        mPlayback!!.stop()
        mMediaSession!!.release()
        Log.d(TAG, "onDestroy: MediaPlayerAdapter stopped, and MediaSession released")
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return BrowserRoot(root, null)
    }

    override fun onLoadChildren(
        parentMediaId: String,
        result: Result<List<MediaBrowserCompat.MediaItem>>
    ) {
        result.sendResult(mediaItems)
    }

    // MediaSession Callback: Transport Controls -> MediaPlayerAdapter
    inner class MediaSessionCallback : MediaSessionCompat.Callback() {
        private val mPlaylist: MutableList<MediaSessionCompat.QueueItem> = ArrayList()
        private var mQueueIndex = -1
        private var mPreparedMedia: MediaMetadataCompat? = null
        override fun onAddQueueItem(description: MediaDescriptionCompat) {
            mPlaylist.add(
                MediaSessionCompat.QueueItem(
                    description,
                    description.hashCode().toLong()
                )
            )
            mQueueIndex = if (mQueueIndex == -1) 0 else mQueueIndex
            mMediaSession!!.setQueue(mPlaylist)
        }

        override fun onRemoveQueueItem(description: MediaDescriptionCompat) {
            mPlaylist.remove(
                MediaSessionCompat.QueueItem(
                    description,
                    description.hashCode().toLong()
                )
            )
            mQueueIndex = if (mPlaylist.isEmpty()) -1 else mQueueIndex
            mMediaSession!!.setQueue(mPlaylist)
        }

        override fun onPrepare() {
            if (mQueueIndex < 0 && mPlaylist.isEmpty()) { // Nothing to play.
                return
            }
            val mediaId = mPlaylist[mQueueIndex].description.mediaId
            mPreparedMedia = getMetadata(this@MusicService, mediaId!!)
            mMediaSession!!.setMetadata(mPreparedMedia)
            if (!mMediaSession!!.isActive) {
                mMediaSession!!.isActive = true
            }
        }

        override fun onPlay() {
            if (!isReadyToPlay) { // Nothing to play.
                return
            }
            if (mPreparedMedia == null) {
                onPrepare()
            }
            mPlayback!!.playFromMedia(mPreparedMedia)
            Log.d(TAG, "onPlayFromMediaId: MediaSession active")
        }

        override fun onPause() {
            mPlayback!!.pause()
        }

        override fun onStop() {
            mPlayback!!.stop()
            mMediaSession!!.isActive = false
        }

        override fun onSkipToNext() {
            mQueueIndex = ++mQueueIndex % mPlaylist.size
            mPreparedMedia = null
            onPlay()
        }

        override fun onSkipToPrevious() {
            mQueueIndex = if (mQueueIndex > 0) mQueueIndex - 1 else mPlaylist.size - 1
            mPreparedMedia = null
            onPlay()
        }

        override fun onSeekTo(pos: Long) {
            mPlayback!!.seekTo(pos)
        }

        private val isReadyToPlay: Boolean
            get() = mPlaylist.isNotEmpty()
    }

    // MediaPlayerAdapter Callback: MediaPlayerAdapter state -> MusicService.
    inner class MediaPlayerListener internal constructor() : PlaybackInfoListener() {
        private val mServiceManager: ServiceManager
        override fun onPlaybackStateChange(state: PlaybackStateCompat) { // Report the state to the MediaSession.
            mMediaSession!!.setPlaybackState(state)
            when (state.state) {
                PlaybackStateCompat.STATE_PLAYING -> mServiceManager.moveServiceToStartedState(state)
                PlaybackStateCompat.STATE_PAUSED -> mServiceManager.updateNotificationForPause(state)
                PlaybackStateCompat.STATE_STOPPED -> mServiceManager.moveServiceOutOfStartedState(
                    state
                )
            }
        }

        internal inner class ServiceManager {
            fun moveServiceToStartedState(state: PlaybackStateCompat) {
                val notification = mPlayback!!.currentMedia?.let {
                    mMediaNotificationManager!!.getNotification(
                        it, state, sessionToken!!
                    )
                }
                if (!mServiceInStartedState) {
                    ContextCompat.startForegroundService(
                        this@MusicService,
                        Intent(this@MusicService, MusicService::class.java)
                    )
                    mServiceInStartedState = true
                }
                startForeground(MediaNotificationManager.NOTIFICATION_ID, notification)
            }

            fun updateNotificationForPause(state: PlaybackStateCompat) {
                stopForeground(false)
                val notification = mPlayback!!.currentMedia?.let {
                    mMediaNotificationManager!!.getNotification(
                        it, state, sessionToken!!
                    )
                }
                mMediaNotificationManager!!.notificationManager
                    .notify(MediaNotificationManager.NOTIFICATION_ID, notification)
            }

            fun moveServiceOutOfStartedState(state: PlaybackStateCompat) {
                stopForeground(true)
                stopSelf()
                mServiceInStartedState = false
            }
        }

        init {
            mServiceManager = ServiceManager()
        }
    }

    companion object {
        private val TAG = MusicService::class.java.simpleName
    }
}