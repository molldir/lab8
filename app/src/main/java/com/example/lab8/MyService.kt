package com.example.lab8

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat

class MyService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private val CHANNEL_ID = "music_channel" // Добавляем константу для ID канала

    override fun onBind(intent: Intent): IBinder? = null

    // Добавляем метод создания канала уведомлений
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for music player notifications"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Music playing")
            .setContentText("Now playing audio track")
            .setSmallIcon(R.drawable.music_icon)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        startForeground(1, notification)

        mediaPlayer = MediaPlayer.create(this, R.raw.sample).apply {
            start()
        }

        return START_STICKY
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }
}