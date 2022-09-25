package com.example.shortenlink.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.example.shortenlink.R

class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_ShortenLinkHelp)
        setContentView(R.layout.activity_help)

        // Assigning the onClickListener of Home Button
        // as backButton Click from the Help Activity
        val homeButton: ImageButton = findViewById(R.id.home_button)
        homeButton.setOnClickListener {
            onBackPressed()
        }
    }
}