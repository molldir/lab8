package com.example.lab8
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var randomCharacterEditText: EditText
    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var serviceIntent: Intent
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var buttonStopMusic: Button

    companion object {
        const val ACTION_TAG = "my.custom.action.tag.lab6"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        randomCharacterEditText = findViewById(R.id.editText_randomCharacter)
        val startButton: Button = findViewById(R.id.button_start)
        val endButton: Button = findViewById(R.id.button_end)
        val musicButton: Button = findViewById(R.id.button_music)
        buttonStopMusic = findViewById(R.id.button_stop_music)

        serviceIntent = Intent(this, RandomCharacterService::class.java)

        startButton.setOnClickListener(::onClick)
        endButton.setOnClickListener(::onClick)
        musicButton.setOnClickListener(::onClickMusic)
        buttonStopMusic.setOnClickListener(::onClickStopMusic)

        broadcastReceiver = MyBroadcastReceiver()

        mediaPlayer = MediaPlayer.create(this, R.raw.song)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.button_start -> startService(serviceIntent)
            R.id.button_end -> {
                stopService(serviceIntent)
                randomCharacterEditText.text = null
            }
        }
    }

    fun onClickMusic(view: View) {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    fun onClickStopMusic(view: View) {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.prepareAsync()
        }
    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter(ACTION_TAG).apply {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                priority = 0
            }
        }
        registerReceiver(broadcastReceiver, intentFilter, Context.RECEIVER_EXPORTED)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val data = intent.getCharExtra("randomCharacter", '?')
            randomCharacterEditText.text = data.toString()
        }
    }
}