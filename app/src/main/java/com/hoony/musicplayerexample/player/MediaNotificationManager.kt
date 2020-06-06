package com.hoony.musicplayerexample.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import com.hoony.musicplayerexample.R
import com.hoony.musicplayerexample.constants.NotificationConstants
import com.hoony.musicplayerexample.main.MainActivity
import com.hoony.musicplayerexample.service.MusicService

/**
 * Keeps track of a notification and updates it automatically for a given MediaSession. This is
 * required so that the music service don't get killed during playback.
 */
class MediaNotificationManager(private val service: MusicService) {
    companion object {
        const val NOTIFICATION_ID = 412
        const val REQUEST_CODE = 501

        val TAG = this::class.simpleName
        val CHANNEL_ID = NotificationConstants.CHANNEL_AUDIO_PLAYER.toString()
    }

    private var playAction: NotificationCompat.Action = NotificationCompat.Action(
        R.drawable.notification_play_arrow,
        "play",
        MediaButtonReceiver.buildMediaButtonPendingIntent(
            service,
            PlaybackStateCompat.ACTION_PLAY
        )
    )
    private var pauseAction: NotificationCompat.Action = NotificationCompat.Action(
        R.drawable.notification_pause,
        "pause",
        MediaButtonReceiver.buildMediaButtonPendingIntent(
            service,
            PlaybackStateCompat.ACTION_PAUSE
        )
    )
    private var stopAction: NotificationCompat.Action = NotificationCompat.Action(
        R.drawable.notification_close,
        "stop",
        MediaButtonReceiver.buildMediaButtonPendingIntent(
            service,
            PlaybackStateCompat.ACTION_STOP
        )
    )
    private var nextAction: NotificationCompat.Action = NotificationCompat.Action(
        R.drawable.ic_skip_next_white_24dp,
        "skip_next",
        MediaButtonReceiver.buildMediaButtonPendingIntent(
            service,
            PlaybackStateCompat.ACTION_SKIP_TO_NEXT
        )
    )
    private var prevAction: NotificationCompat.Action = NotificationCompat.Action(
        R.drawable.ic_skip_previous_white_24dp,
        "play",
        MediaButtonReceiver.buildMediaButtonPendingIntent(
            service,
            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
        )
    )

    private var notificationManager: NotificationManager =
        service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        // Cancel all notifications to handle the case where the Service was killed and
        // restarted by the system.
        notificationManager.cancelAll()
    }

    fun getNotificationManager(
        metadata: MediaMetadataCompat,
        state: PlaybackStateCompat,
        token: MediaSessionCompat.Token
    ): Notification {
        val isPlaying = state.state == PlaybackStateCompat.STATE_PLAYING
        val description = metadata.description
        val builder = buildNotification(state, token, isPlaying, description)
        return builder.build()
    }

    private fun buildNotification(
        state: PlaybackStateCompat,
        token: MediaSessionCompat.Token,
        isPlaying: Boolean,
        description: MediaDescriptionCompat
    ): NotificationCompat.Builder {
        // Create the (mandatory) notification channel when running on Android Oreo.
    }

    // Does nothing on versions of Android earlier than O.
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            // The user-visible name of the channel.
            val name = "MediaSession"
            // The user-visible description of the channel.
            val description = "MediaSession and MusicPlayer"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            // Configure the notification channel.
            channel.description = description
            channel.enableLights(true)
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            channel.lightColor = Color.RED
            channel.enableVibration(true)
        }
    }

    private fun isOOrHigher(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

    private fun createContentIntent(): PendingIntent {
        val openUI = Intent(service, MainActivity::class.java)
        openUI.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        return PendingIntent.getActivity(
            service,
            REQUEST_CODE,
            openUI,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
    }
}