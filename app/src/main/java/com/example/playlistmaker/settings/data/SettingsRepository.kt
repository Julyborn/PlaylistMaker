package com.example.playlistmaker.settings.data

interface SettingsRepository {
    fun isDarkThemeEnabled(): Boolean
    fun setDarkTheme(enabled: Boolean)
}