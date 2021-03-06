package com.hoony.musicplayerexample.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.hoony.musicplayerexample.R
import com.hoony.musicplayerexample.player.MediaBrowserHelper
import com.hoony.musicplayerexample.player.MusicLibrary
import com.hoony.musicplayerexample.service.MusicService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener,
    MusicListAdapter.OnItemClickListener {

    private val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    private val viewModel: MainViewModel by viewModels()

    private val mediaBrowserHelper: MediaBrowserHelper = MediaBrowserConnection(this)

    private var isFirstLoading: Boolean = false
    private var isPlaying: Boolean = false

    override fun onStart() {
        super.onStart()
        mediaBrowserHelper.onStart()
    }

    override fun onStop() {
        super.onStop()
        seekBar.disconnectController()
        mediaBrowserHelper.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in this.permissions) {
                if (checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(permissions, 0)
                    return
                }
            }
        }

        createView()
    }

    private fun createView() {
        setContentView(R.layout.activity_main)

        setView()
        setObserver()
        setListener()

        mediaBrowserHelper.registerCallback(MediaBrowserListener())
    }

    private fun setView() {
        vpAlbum.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        rvAlbumList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        rvAlbumList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun setObserver() {
        viewModel.albumListLiveData.observe(
            this,
            Observer {
                vpAlbum.adapter = AlbumPagerAdapter(it)
            }
        )
        viewModel.musicListLiveData.observe(
            this,
            Observer {
                rvAlbumList.adapter = MusicListAdapter(it, this@MainActivity)
            }
        )
        viewModel.playMusicLiveData.observe(
            this,
            Observer {

            }
        )
    }

    private fun setListener() {
        vpAlbum.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.getMusicList(position)
            }
        })
        ivPlayPause.setOnClickListener(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            for (result in grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    AlertDialog.Builder(this)
                        .setTitle(getString(R.string.permission_dialog_title))
                        .setMessage(getString(R.string.permission_dialog_message))
                        .setPositiveButton(getString(R.string.setting)) { _, _ ->
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", packageName, null)
                            )
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                        .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                            finish()
                        }
                        .show()
                    return
                }
            }
        }

        createView()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.ivPlayPause -> {

            }
            R.id.ivPrev -> {

            }
            R.id.ivNext -> {

            }
        }
    }

    override fun onItemClick(position: Int) {
        viewModel.setPlayMusic(position)
    }

    private inner class MediaBrowserConnection(private val context: Context) :
        MediaBrowserHelper(context, MusicService::class.java) {

        override fun onConnected(mediaController: MediaControllerCompat) {
            super.onConnected(mediaController)

            val state: PlaybackStateCompat = mediaController.playbackState

            viewModel.isPlaying = state.state == PlaybackStateCompat.STATE_PLAYING
            togglePlayPause(viewModel.isPlaying)

            seekBar.setMediaController(this@MainActivity, mediaController)
        }

        override fun onChildrenLoaded(
            parentId: String,
            children: List<MediaBrowserCompat.MediaItem>
        ) {
            super.onChildrenLoaded(parentId, children)

            val mediaController: MediaControllerCompat = mediaController

            // Queue 에 Media item 추가
            for (mediaItem in children) {
                mediaController.addQueueItem(mediaItem.description)
            }

            mediaController.transportControls.prepare()
        }
    }

    private inner class MediaBrowserListener : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {

            if (isFirstLoading) {
                isFirstLoading = true
                return
            }

            val changedState: Boolean = state?.state == PlaybackStateCompat.STATE_PLAYING
            togglePlayPause(changedState)

            isPlaying = changedState
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            if (metadata != null) {
                tvTitle.text = metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
                Glide.with(this@MainActivity)
                    .load(
                        MusicLibrary.getAlbumBitmap(
                            this@MainActivity,
                            metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
                        )
                    )
                    .into(ivAlbumArt)
            }
        }
    }

    private fun togglePlayPause(isPlaying: Boolean) {
        if (isPlaying) {
            Glide.with(this)
                .load(R.drawable.notification_pause)
                .into(ivPlayPause)
        } else {
            Glide.with(this)
                .load(R.drawable.notification_play_arrow)
                .into(ivPlayPause)
        }
    }
}
