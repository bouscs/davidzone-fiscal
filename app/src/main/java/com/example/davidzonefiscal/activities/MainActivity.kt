package com.example.davidzonefiscal.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.davidzonefiscal.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        Handler().postDelayed({
            val intent = Intent(this@MainActivity, MapsActivity::class.java)
            startActivity(intent)
            finish()
        }, 1000)

    }
}