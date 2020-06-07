package com.hoony.musicplayerexample.service

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.hoony.musicplayerexample.player.MediaNotificationManager
import com.hoony.musicplayerexample.player.PlayerAdapter

class MusicService : MediaBrowserServiceCompat() {

    companion object {
        private val TAG = MusicService::class.simpleName
    }

    private val mediaSession: MediaSessionCompat = MediaSessionCompat(this, "MusicService")
    private val playback: PlayerAdapter? = null
    private val mediaNotificationManager = MediaNotificationManager(this)

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onGetRoot(p0: String, p1: Int, p2: Bundle?): BrowserRoot? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    open inner class MediaSessionCallback : MediaSessionCompat.Callback() {

        private val playList = ArrayList<MediaSessionCompat.QueueItem>()
        private var queueIndex: Int = -1
        private var preparedMedia: MediaMetadataCompat? = null

        override fun onAddQueueItem(description: MediaDescriptionCompat?) {
            playList.add(
                MediaSessionCompat.QueueItem(
                    description,
                    description.hashCode().toLong()
                )
            )
            queueIndex = if (queueIndex == -1) 0 else queueIndex
            mediaSession.setQueue(playList)
        }

        override fun onRemoveQueueItem(description: MediaDescriptionCompat?) {
            playList.remove(
                MediaSessionCompat.QueueItem(
                    description,
                    description.hashCode().toLong()
                )
            )
            queueIndex = if (playList.isEmpty()) -1 else queueIndex
            mediaSession.setQueue(playList)
        }

        override fun onPrepare() {
            if (queueIndex < 0 && playList.isEmpty()) {
                // Nothing to play.
                return
            }

            val mediaId = playList.get(queueIndex).description.mediaId

        }
    }
}