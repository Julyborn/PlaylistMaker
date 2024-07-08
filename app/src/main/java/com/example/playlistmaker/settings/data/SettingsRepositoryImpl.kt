package com.example.playlistmaker.settings.data

import com.example.playlistmaker.App
import com.example.playlistmaker.settings.domain.SettingsRepository

class SettingsRepositoryImpl : SettingsRepository {

    override fun isDarkThemeEnabled(): Boolean {
        return App.sharedPreferences.getBoolean(App.DARK_THEME, false)
    }

    override fun setDarkTheme(enabled: Boolean) {
        App.sharedPreferences.edit().putBoolean(App.DARK_THEME, enabled).apply()
    }
}