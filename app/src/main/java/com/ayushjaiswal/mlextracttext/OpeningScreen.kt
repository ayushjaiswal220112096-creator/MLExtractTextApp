package com.ayushjaiswal.mlextracttext

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class OpeningScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_opening_screen)

        supportActionBar?.hide()

        Handler(Looper.getMainLooper()).postDelayed({

          val i = Intent(this,MainActivity::class.java)
            startActivity(i)
            finish()
        },3000)



    }
}