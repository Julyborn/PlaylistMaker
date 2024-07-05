package com.example.playlistmaker.settings.data

import android.content.Context
import com.example.playlistmaker.App

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {

    override fun isDarkThemeEnabled(): Boolean {
        return App.sharedPreferences.getBoolean(App.DARK_THEME, false)
    }

    override fun setDarkTheme(enabled: Boolean) {
        App.sharedPreferences.edit().putBoolean(App.DARK_THEME, enabled).apply()
    }
}