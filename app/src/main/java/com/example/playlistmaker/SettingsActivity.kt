package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val backImage = findViewById<ImageView>(R.id.back_button)
        backImage.setOnClickListener {
            finish()
        }

        val switchDarkTheme: Switch = findViewById(R.id.switchDarkTheme)
        switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

        }

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
            putExtra(Intent.EXTRA_TEXT, getString(R.string.course_link))
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, "Share via"))
    }

    private fun writeToSupport() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject_to_support))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.email_message_to_support))
        }
        startActivity(emailIntent)
    }

    private fun openUserAgreement() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.legal_practicum_offer_link)))
        startActivity(browserIntent)
    }

}