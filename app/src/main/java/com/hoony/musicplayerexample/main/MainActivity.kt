package com.hoony.musicplayerexample.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.hoony.musicplayerexample.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    enum class Tab(val id: Int) {
        HOME(R.id.tab_home)
    }

    private val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    private val viewModel: MainViewModel by viewModels()

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
                rvAlbumList.adapter = MusicListAdapter(it)
            }
        )
    }

    private fun setListener() {
        vpAlbum.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.getMusicList(position)
            }
        })
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
}
