package com.example.playlistmaker.settings.presentation
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.SettingsInteractor

class SettingsViewModel(private val settingsInteractor: SettingsInteractor) : ViewModel() {

    private val _isDarkThemeEnabled = MutableLiveData<Boolean>()
    val isDarkThemeEnabled: LiveData<Boolean> get() = _isDarkThemeEnabled

    fun loadSettings() {
        _isDarkThemeEnabled.value = settingsInteractor.isDarkThemeEnabled()
    }

    fun setDarkTheme(enabled: Boolean) {
        settingsInteractor.setDarkTheme(enabled)
        _isDarkThemeEnabled.value = enabled
    }

    fun shareApp(): String {
        return "Share this app: ${"course_link"}"
    }

    fun getSupportEmailData(): Pair<String, String> {
        return Pair("support_email@example.com", "Support Email Subject")
    }

    fun getUserAgreementUrl(): String {
        return "https://legal_practicum_offer_link"
    }
}
