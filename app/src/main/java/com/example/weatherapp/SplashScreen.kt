package com.example.weatherapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {

    private val splashTimeOut: Long = 1500
    private lateinit var logoImageView: ImageView
    private lateinit var frameLayout: FrameLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        frameLayout = findViewById(R.id.frameLayout)
        logoImageView = findViewById(R.id.logoImageView)

        logoImageView.scaleX = 0.8f
        logoImageView.scaleY = 0.8f
        logoImageView.alpha = 0f

        logoImageView.animate()
            .alpha(1f)
            .scaleX(1.0f)
            .scaleY(1.0f)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setDuration(2000)
            .start()

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, splashTimeOut)
    }
}
