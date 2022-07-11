package com.example.shortenlink.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.example.shortenlink.R

class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        val homeButton: ImageButton = findViewById(R.id.home_button)
        homeButton.setOnClickListener {
            onBackPressed()
        }
    }
}