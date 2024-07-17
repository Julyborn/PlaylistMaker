package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.di.libraryModule
import com.example.playlistmaker.di.playerModule
import com.example.playlistmaker.di.searchModule
import com.example.playlistmaker.di.settingsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application(){
    companion object {
        const val DARK_THEME = "dark_theme_key"
        const val APPLICATION_PREFERENCES = "application_preferences"
        lateinit var sharedPreferences: SharedPreferences
        const val BASE_URL = "https://itunes.apple.com"
        const val HISTORY_TRACK_LIST_KEY = "KEY_FOR_HISTORY_LIST"
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(playerModule, searchModule, settingsModule, libraryModule)
        }
        sharedPreferences = getSharedPreferences(APPLICATION_PREFERENCES, MODE_PRIVATE)
        val darkTheme = sharedPreferences.getBoolean(DARK_THEME, false)
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO) }


}