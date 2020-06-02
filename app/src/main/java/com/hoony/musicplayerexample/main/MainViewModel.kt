package com.hoony.musicplayerexample.main

import android.app.Application
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.*
import com.hoony.musicplayerexample.data.Album
import com.hoony.musicplayerexample.data.Music

class MainViewModel(application: Application, private val state: SavedStateHandle) :
    AndroidViewModel(application) {

    val albumListLiveData: LiveData<List<Album>> = liveData {
        val audioInfo = getAudioInfo()

        val albumList = ArrayList<Album>()

        audioInfo?.let {
            val idIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)
            val nameIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)
            val artistIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)
            val numOfSongIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)

            while (it.moveToNext()) {
                albumList.add(
                    Album(
                        ContentUris.withAppendedId(
                            Uri.parse("content://media/external/audio/albumart"),
                            it.getLong(idIndex)
                        ),
                        it.getString(nameIndex),
                        it.getString(artistIndex),
                        it.getInt(numOfSongIndex)
                    )
                )
            }
        }

        this.emit(albumList as List<Album>)
    }

    private fun getAudioInfo(): Cursor? {
        return getApplication<Application>().contentResolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null,
            null
        )
    }

    private val _musicListMutableLiveData = MutableLiveData<List<Music>>()
    val musicListLiveData: LiveData<List<Music>>
        get() = _musicListMutableLiveData

    fun getMusicList(position: Int) {
        val album = this.albumListLiveData.value?.get(position)
        album?.let {
            val musicInfo = getMusicInfo(album)

            musicInfo?.let {
                val musicList = arrayListOf<Music>()

                val idIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

                while (it.moveToNext()) {
                    musicList.add(
                        Music(
                            ContentUris.withAppendedId(
                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                it.getLong(idIndex)
                            ),
                            it.getString(titleIndex),
                            it.getInt(artistIndex)
                        )
                    )
                }

                _musicListMutableLiveData.postValue(musicList)
            }
        }
    }

    private fun getMusicInfo(album: Album): Cursor? {
        return getApplication<Application>().contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION
            ),
            "${MediaStore.Audio.Media.ALBUM} = ?",
            arrayOf(
                album.name
            ),
            null,
            null
        )
    }
}