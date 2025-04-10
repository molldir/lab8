package com.example.lab8

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class MyService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var notificationManager: NotificationManager

    companion object {
        const val CHANNEL_ID = "music_player_channel"
        const val NOTIFICATION_ID = 101
        const val ACTION_STOP = "STOP_MUSIC"
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                CHANNEL_ID,
                "Music Player",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Channel for music playback"
                setSound(null, null)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                notificationManager.createNotificationChannel(this)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> stopMusic()
            else -> startMusic()
        }
        return START_STICKY
    }

    private fun startMusic() {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(applicationContext,
                        Uri.parse("android.resource://$packageName/${R.raw.top_song}"))
                    setOnPreparedListener {
                        start()
                        showNotification("Now playing")
                    }
                    setOnErrorListener { _, what, extra ->
                        Log.e("MyService", "Error $what, $extra")
                        true
                    }
                    prepareAsync()
                }
            } else if (!mediaPlayer!!.isPlaying) {
                mediaPlayer?.start()
                showNotification("Now playing")
            }
        } catch (e: Exception) {
            Log.e("MyService", "Error starting music", e)
            stopSelf()
        }
    }

    private fun stopMusic() {
        mediaPlayer?.pause()
        showNotification("Music paused")
    }

    private fun showNotification(text: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Music Player")
            .setContentText(text)
            .setSmallIcon(R.drawable.music_icon)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}