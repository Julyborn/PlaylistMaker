package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App : Application(){
    companion object {
        const val DARK_THEME = "dark_theme_key"
        const val APPLICATION_PREFERENCES = "application_preferences"
        lateinit var sharedPreferences: SharedPreferences
    }

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences(APPLICATION_PREFERENCES, MODE_PRIVATE)
        val darkTheme = sharedPreferences.getBoolean(DARK_THEME, false)
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO) }


}