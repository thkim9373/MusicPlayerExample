package com.hoony.musicplayerexample.view_model

import android.app.Application
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.liveData
import com.hoony.musicplayerexample.data.Album

class AlbumListViewModel(private val savedStateHandle: SavedStateHandle, application: Application) :
    AndroidViewModel(application) {

    private val audioInfoCursor: Cursor? by lazy {
        application.contentResolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null,
            null
        )
    }

    val albumListLiveData: LiveData<List<Album>> = liveData {
        val albumList = ArrayList<Album>()

        audioInfoCursor?.let {
            val idIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)
            val artistIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)
            val numOfSongIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)

            while (it.moveToNext()) {
                albumList.add(
                    Album(
                        ContentUris.withAppendedId(
                            Uri.parse("content://media/external/audio/albumart"),
                            it.getLong(idIndex)
                        ),
                        it.getString(artistIndex),
                        it.getInt(numOfSongIndex)
                    )
                )
            }
        }

        this.emit(albumList as List<Album>)
    }
}