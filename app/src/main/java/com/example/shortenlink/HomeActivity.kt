package com.example.shortenlink

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.FrameLayout

class HomeActivity : AppCompatActivity() {
    lateinit var shortenButton: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        shortenButton = findViewById(R.id.shorten_button)
        shortenButton.startAnimation(
            AnimationUtils.loadAnimation(this,R.anim.button_anim)
        )
    }
}