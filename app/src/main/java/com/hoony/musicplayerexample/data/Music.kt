package com.hoony.musicplayerexample.data

import android.net.Uri

data class Music(
    val uri: Uri,
    val title: String,
    val duration: Int
)