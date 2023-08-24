package com.example.weatherapp

import android.app.Application

class Application  : Application() {

    companion object {
        lateinit var instance: Application
    }

    override fun onCreate() {
        super.onCreate()
        //spremi instance of application kad je created
        instance = this
    }

    override fun onTerminate() {
        super.onTerminate()

        //clear city u prefs kad je terminiramo
        val sharedPrefs = Preferences.getInstance(this)
        sharedPrefs.clearCityValue()
    }
}

