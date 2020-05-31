package com.hoony.musicplayerexample.activity

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
import androidx.recyclerview.widget.GridLayoutManager
import com.hoony.musicplayerexample.R
import com.hoony.musicplayerexample.view_model.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_album_list.*

class MainActivity : AppCompatActivity() {

    private val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    private val viewModel by viewModels<MainViewModel>()

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

        rvAlbumList.layoutManager = GridLayoutManager(this, 2)

        setObserver()
    }

    private fun setObserver() {
        viewModel.fragmentsStackLiveData.observe(
            this,
            Observer {
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(mainFrameLayout.id, it.peek()).commit()
            }
        )
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
