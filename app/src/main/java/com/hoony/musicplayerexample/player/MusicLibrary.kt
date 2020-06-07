package com.hoony.musicplayerexample.player

import android.content.ContentResolver
import android.support.v4.media.MediaMetadataCompat
import com.hoony.musicplayerexample.BuildConfig
import java.util.*
import kotlin.collections.HashMap

open class MusicLibrary {
    companion object {
        private val music = TreeMap<String, MediaMetadataCompat>()
        private val albumRes = HashMap<String, Int>()
        private val musicFileName = HashMap<String, String>()

        fun getRoot(): String {
            return "root"
        }

        fun getAlbumArtUri(albumArtRedName: String): String {
            return ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    BuildConfig.APPLICATION_ID + "/drawable/" + albumArtRedName
        }

        fun getMusicFileName(mediaId: String): String? {
            return if (musicFileName.containsKey(mediaId))
                musicFileName[mediaId]
            else
                null
        }

        fun getAlbumRes(mediaId: String): Int? {
            return if (albumRes.containsKey(mediaId))
                albumRes[mediaId]
            else
                null
        }
    }
}