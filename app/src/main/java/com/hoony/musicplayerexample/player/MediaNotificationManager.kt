package com.hoony.musicplayerexample.player

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.hoony.musicplayerexample.constants.Notification
import com.hoony.musicplayerexample.service.MusicService

class MediaNotificationManager {
    companion object {
        const val NOTIFICATION_ID = 412
        const val REQUEST_CODE = 501

        val TAG = this::class.simpleName
        val CHANNEL_ID = Notification.CHANNEL_AUDIO_PLAYER.toString()
    }

    private var service: MusicService? = null
    private var playAction: NotificationCompat.Action? = null
    private var pauseAction: NotificationCompat.Action? = null
    private var stopAction: NotificationCompat.Action? = null
    private var nextAction: NotificationCompat.Action? = null
    private var prevAction: NotificationCompat.Action? = null

    private var notificationManager: NotificationManagerCompat? = null
}