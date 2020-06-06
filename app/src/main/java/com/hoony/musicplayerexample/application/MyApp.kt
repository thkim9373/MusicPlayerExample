package com.hoony.musicplayerexample.application

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.hoony.musicplayerexample.R
import com.hoony.musicplayerexample.constants.NotificationConstants

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (notificationManager.getNotificationChannel(NotificationConstants.CHANNEL_AUDIO_PLAYER.toString()) == null) {
                val notificationChannel = NotificationChannel(
                    NotificationConstants.CHANNEL_AUDIO_PLAYER.toString(),
                    resources.getString(R.string.notification_name_audio_player),
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationChannel.lockscreenVisibility =
                    android.app.Notification.VISIBILITY_PUBLIC

                notificationManager.createNotificationChannel(notificationChannel)
            }
        }
    }
}