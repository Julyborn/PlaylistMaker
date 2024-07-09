package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        val switchDarkTheme: Switch = findViewById(R.id.switchDarkTheme)
        viewModel.isDarkThemeEnabled.observe(this, Observer { isEnabled ->
            switchDarkTheme.isChecked = isEnabled
        })
        switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDarkTheme(isChecked)
        }

        viewModel.loadSettings()

        val shareButton: FrameLayout = findViewById(R.id.shareButton)
        shareButton.setOnClickListener {
            shareApp()
        }

        val supportButton: FrameLayout = findViewById(R.id.supportButton)
        supportButton.setOnClickListener {
            writeToSupport()
        }

        val agreementButton: FrameLayout = findViewById(R.id.agreementButton)
        agreementButton.setOnClickListener {
            openUserAgreement()
        }
    }

    private fun shareApp() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, viewModel.shareApp())
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, "Share via"))
    }

    private fun writeToSupport() {
        val (email, subject) = viewModel.getSupportEmailData()
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, getString(R.string.email_message_to_support))
        }
        startActivity(emailIntent)
    }

    private fun openUserAgreement() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(viewModel.getUserAgreementUrl()))
        startActivity(browserIntent)
    }
}
