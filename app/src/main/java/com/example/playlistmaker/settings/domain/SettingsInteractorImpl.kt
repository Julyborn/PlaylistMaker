package com.example.playlistmaker.settings.domain

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository) : SettingsInteractor {

    override fun isDarkThemeEnabled(): Boolean {
        return settingsRepository.isDarkThemeEnabled()
    }

    override fun setDarkTheme(enabled: Boolean) {
        settingsRepository.setDarkTheme(enabled)
    }
}
