package com.example.playlistmaker.settings.presentation

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.domain.SettingsInteractor

class SettingsViewModel(
    application: Application,
    private val settingsInteractor: SettingsInteractor
) : AndroidViewModel(application) {

    private val _isDarkThemeEnabled = MutableLiveData<Boolean>()
    val isDarkThemeEnabled: LiveData<Boolean> get() = _isDarkThemeEnabled

    fun loadSettings() {
        _isDarkThemeEnabled.value = settingsInteractor.isDarkThemeEnabled()
    }

    fun setDarkTheme(enabled: Boolean) {
        settingsInteractor.setDarkTheme(enabled)
        _isDarkThemeEnabled.value = enabled
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    fun shareApp(): String {
        val app = getApplication<Application>()
        return app.getString(R.string.share_app_template, app.getString(R.string.course_link))
    }

    fun getSupportEmailData(): Pair<String, String> {
        val app = getApplication<Application>()
        val email = app.getString(R.string.support_email)
        val subject = app.getString(R.string.email_subject_to_support)
        return Pair(email, subject)
    }

    fun getUserAgreementUrl(): String {
        val app = getApplication<Application>()
        return app.getString(R.string.legal_practicum_offer_link)
    }
}
