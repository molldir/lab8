package com.example.lab8

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.registerReceiver
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button_start).setOnClickListener {
            // Заглушка для теста
            Toast.makeText(this, "Start clicked", Toast.LENGTH_SHORT).show()
        }
        // Добавляем в onCreate
        val serviceIntent = Intent(this, RandomCharacterService::class.java)

        findViewById<Button>(R.id.button_start).setOnClickListener {
            startService(serviceIntent)
        }

        findViewById<Button>(R.id.button_end).setOnClickListener {
            stopService(serviceIntent)
        }
        val musicIntent = Intent(this, MyService::class.java)
        findViewById<Button>(R.id.button_music).setOnClickListener {
            startService(musicIntent)
        }
        findViewById<Button>(R.id.button_stop_music).setOnClickListener {
            stopService(musicIntent)
        }
    }

}


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val char = intent.getCharExtra("char", '?')
            findViewById<EditText>(R.id.editText_randomCharacter).setText(char.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(receiver, IntentFilter("RANDOM_CHAR_ACTION"))
    }

}
