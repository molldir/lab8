package com.example.lab8

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.util.Random

class RandomCharacterService : Service() {
    private var isRunning = false
    private val random = Random()
    private val alphabet = ('A'..'Z').toList()

    companion object {
        const val ACTION_TAG = "my.custom.action.tag.lab6"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isRunning) {
            isRunning = true
            Thread {
                while (isRunning) {
                    try {
                        Thread.sleep(1000)
                        val randomChar = alphabet[random.nextInt(alphabet.size)]
                        sendBroadcast(Intent(ACTION_TAG).apply {
                            putExtra("randomCharacter", randomChar)
                        })
                    } catch (e: InterruptedException) {
                        Log.e("RandomCharacterService", "Thread interrupted", e)
                    }
                }
            }.start()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        isRunning = false
        super.onDestroy()
    }
}