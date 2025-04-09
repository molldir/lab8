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
    private var soundPlayer: MediaPlayer? = null
    private var notificationManager: NotificationManager? = null

    companion object {
        const val CHANNEL_ID = "foreground_service_channel"
        private const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        initializeMediaPlayer()
    }

    private fun initializeMediaPlayer() {
        try {
            val afd: AssetFileDescriptor = resources.openRawResourceFd(R.raw.song)
                ?: throw IllegalStateException("Аудио файл не найден в res/raw.")

            soundPlayer = MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                prepare()
                isLooping = true
                start()
            }
            Log.i("MyService", "Медиаплеер инициализирован. Длительность = ${soundPlayer?.duration}")
        } catch (e: Exception) {
            handleMediaPlayerError(e)
        }
    }

    private fun handleMediaPlayerError(e: Exception) {
        Log.e("MyService", "Ошибка инициализации MediaPlayer", e)
        Toast.makeText(this, "Ошибка запуска музыки", Toast.LENGTH_SHORT).show()
        stopSelf()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showServiceStartedMessage()
        startForegroundService()
        handleStopCommand(intent)
        return START_STICKY
    }

    private fun showServiceStartedMessage() {
        Toast.makeText(this, "Сервис запущен", Toast.LENGTH_SHORT).show()
        Log.i("MyService", "Сервис запущен...")
    }

    private fun startForegroundService() {
        createNotificationChannel()
        val notification = buildNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Сервис воспроизведения музыки",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Канал для воспроизведения музыки"
            }
            notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun buildNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(R.drawable.music_icon)
        .setContentTitle("Музыкальный сервис")
        .setContentText("Идет воспроизведение музыки...")
        .setOngoing(true)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()

    private fun handleStopCommand(intent: Intent?) {
        if (intent?.action == "STOP_MUSIC") {
            stopMusic()
        }
    }

    private fun stopMusic() {
        soundPlayer?.run {
            if (isPlaying) {
                stop()
                release()
                soundPlayer = null
                stopForeground(true)
                stopSelf()
                showMusicStoppedMessage()
            }
        }
    }

    private fun showMusicStoppedMessage() {
        Toast.makeText(this, "Музыка остановлена", Toast.LENGTH_SHORT).show()
        Log.i("MyService", "Музыка остановлена")
    }

    override fun onDestroy() {
        cleanupMediaPlayer()
        showServiceStoppedMessage()
        super.onDestroy()
    }

    private fun cleanupMediaPlayer() {
        soundPlayer?.run {
            stop()
            release()
        }
    }

    private fun showServiceStoppedMessage() {
        Toast.makeText(this, "Сервис остановлен", Toast.LENGTH_SHORT).show()
        Log.i("MyService", "Сервис остановлен...")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}