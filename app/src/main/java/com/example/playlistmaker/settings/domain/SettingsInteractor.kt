package com.example.playlistmaker.settings.domain

interface SettingsInteractor {
    fun isDarkThemeEnabled(): Boolean
    fun setDarkTheme(enabled: Boolean)
}