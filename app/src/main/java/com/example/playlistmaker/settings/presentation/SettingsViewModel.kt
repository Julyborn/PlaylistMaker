package com.example.playlistmaker.settings.presentation
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.SettingsInteractor

class SettingsViewModel(private val settingsInteractor: SettingsInteractor) : ViewModel() {

    private val _isDarkThemeEnabled = MutableLiveData<Boolean>()
    private val app = Creator.application

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
        return app.getString(R.string.share_app_template, app.getString(R.string.course_link))
    }

    fun getSupportEmailData(): Pair<String, String> {
        val email = app.getString(R.string.support_email)
        val subject = app.getString(R.string.email_subject_to_support)
        return Pair(email, subject)
    }

    fun getUserAgreementUrl(): String {
        return app.getString(R.string.legal_practicum_offer_link)
    }
}
