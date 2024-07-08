package com.example.playlistmaker.settings.domain

interface SettingsRepository {
    fun isDarkThemeEnabled(): Boolean
    fun setDarkTheme(enabled: Boolean)
}