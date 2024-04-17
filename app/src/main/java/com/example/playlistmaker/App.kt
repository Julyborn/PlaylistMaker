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

    var darkTheme = false


    fun switchTheme(isDarkThemeEnabled: Boolean) {
        darkTheme = isDarkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        sharedPreferences.edit()
            .putBoolean(DARK_THEME, darkTheme)
            .apply()
    }


    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences(APPLICATION_PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPreferences.getBoolean(DARK_THEME, false)
        switchTheme(darkTheme)
    }


}