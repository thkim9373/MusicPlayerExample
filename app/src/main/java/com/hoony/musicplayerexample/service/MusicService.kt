package com.hoony.musicplayerexample.service

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.hoony.musicplayerexample.player.PlayerAdapter

class MusicService : MediaBrowserServiceCompat() {

    companion object {
        private val TAG = MusicService::class.simpleName
    }

    private val mediaSession: MediaSessionCompat? = null
    private val playback: PlayerAdapter? = null

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onGetRoot(p0: String, p1: Int, p2: Bundle?): BrowserRoot? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}