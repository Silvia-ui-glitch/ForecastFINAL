package com.example.weatherapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        //ucitavanje fragmenta
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainer, WeatherToday())
                commit()
            }
        }
    }
}
