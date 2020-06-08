package com.hoony.musicplayerexample.player

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import com.hoony.musicplayerexample.BuildConfig
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
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

        fun getAlbumBitmap(context: Context, mediaId: String): Bitmap? {
            this.getAlbumRes(mediaId)?.let {
                return BitmapFactory.decodeResource(
                    context.resources,
                    it
                )
            }
            return null
        }

        fun getMediaItems(): List<MediaBrowserCompat.MediaItem> {
            val result: ArrayList<MediaBrowserCompat.MediaItem> = ArrayList()
            for (metaData in music.values) {
                result.add(
                    MediaBrowserCompat.MediaItem(
                        metaData.description,
                        MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
                    )
                )
            }
            return result
        }

        fun getMetadata(context: Context, mediaId: String): MediaMetadataCompat {
            val metadataWithoutBitmap = music[mediaId]
            val albumArt = getAlbumBitmap(context, mediaId)

            // Since MediaMetadataCompat is immutable, we need to create a copy to set the album art.
            // We don't set it initially on all items so that they don't take unnecessary memory.
            val builder: MediaMetadataCompat.Builder = MediaMetadataCompat.Builder()
            for (key in arrayOf(
                MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
                MediaMetadataCompat.METADATA_KEY_ALBUM,
                MediaMetadataCompat.METADATA_KEY_ARTIST,
                MediaMetadataCompat.METADATA_KEY_GENRE,
                MediaMetadataCompat.METADATA_KEY_TITLE
            )) {
                builder.putString(key, metadataWithoutBitmap?.getString(key))
            }
            metadataWithoutBitmap?.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)?.let {
                builder.putLong(
                    MediaMetadataCompat.METADATA_KEY_DURATION,
                    it
                )
            }
            builder.putBitmap(
                MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                albumArt
            )
            return builder.build()
        }

        fun createMediaMetadataCompat(
            mediaId: String,
            title: String,
            artist: String,
            album: String,
            genre: String,
            duration: Long,
            durationUnit: TimeUnit,
            musicFileName: String,
            albumArtResId: Int,
            albumArtResName: String
        ) {
            this.music[mediaId] = MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                .putLong(
                    MediaMetadataCompat.METADATA_KEY_DURATION,
                    TimeUnit.MILLISECONDS.convert(duration, durationUnit)
                )
                .putString(MediaMetadataCompat.METADATA_KEY_GENRE, genre)
                .putString(
                    MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                    getAlbumArtUri(albumArtResName)
                )
                .putString(
                    MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI,
                    getAlbumArtUri(albumArtResName)
                )
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                .build()
            this.albumRes[mediaId] = albumArtResId
            this.musicFileName[mediaId] = musicFileName
        }
    }
}