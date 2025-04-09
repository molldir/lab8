package com.example.lab8

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log


class RandomCharacterService : Service() {
    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Thread {
            while (true) {
                Thread.sleep(1000)
                Log.d("Service", "Generated: A") // Заглушка
            }
        }.start()

        return START_STICKY
    }
}