package com.example.lab8

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
    }
}