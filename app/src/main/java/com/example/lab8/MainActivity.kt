package com.example.lab8

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    private lateinit var randomCharacterEditText: EditText
    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var serviceIntent: Intent
    private lateinit var musicIntent: Intent
    private var mediaPlayer: MediaPlayer? = null

    companion object {
        const val ACTION_TAG = "my.custom.action.tag.lab6"
        private const val NOTIFICATION_PERMISSION_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupIntents()
        setupMediaPlayer()
        setupBroadcastReceiver()
        requestNotificationPermission()
    }

    private fun initViews() {
        randomCharacterEditText = findViewById(R.id.editText_randomCharacter)
        findViewById<Button>(R.id.button_start).setOnClickListener { startService(serviceIntent) }
        findViewById<Button>(R.id.button_end).setOnClickListener {
            stopService(serviceIntent)
            randomCharacterEditText.text = null
        }
        findViewById<Button>(R.id.button_music).setOnClickListener { startService(musicIntent) }
        findViewById<Button>(R.id.button_stop_music).setOnClickListener {
            val stopIntent = Intent(this, MyService::class.java).apply {
                action = MyService.ACTION_STOP
            }
            startService(stopIntent)
        }
    }



    private fun setupIntents() {
        serviceIntent = Intent(this, RandomCharacterService::class.java)
        musicIntent = Intent(this, MyService::class.java)
    }

    private fun setupMediaPlayer() {
        mediaPlayer = MediaPlayer.create(this, R.raw.top_song).apply {
            setOnErrorListener { _, _, _ ->
                Toast.makeText(this@MainActivity, "Playback error", Toast.LENGTH_SHORT).show()
                true
            }
        }
    }

    private fun setupBroadcastReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val data = intent.getCharExtra("randomCharacter", '?')
                randomCharacterEditText.setText(data.toString())
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_CODE
            )
        }
    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter(ACTION_TAG)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(broadcastReceiver, intentFilter, Context.RECEIVER_EXPORTED)
        } else {
            registerReceiver(broadcastReceiver, intentFilter)
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }

}