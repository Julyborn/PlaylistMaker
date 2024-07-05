package com.example.playlistmaker.settings.domain

import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.data.SettingsRepository

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository) : SettingsInteractor {

    override fun isDarkThemeEnabled(): Boolean {
        return settingsRepository.isDarkThemeEnabled()
    }

    override fun setDarkTheme(enabled: Boolean) {
        settingsRepository.setDarkTheme(enabled)
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
