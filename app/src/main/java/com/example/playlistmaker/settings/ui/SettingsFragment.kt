package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val switchDarkTheme: Switch = view.findViewById(R.id.switchDarkTheme)
        viewModel.isDarkThemeEnabled.observe(viewLifecycleOwner, Observer { isEnabled ->
            switchDarkTheme.isChecked = isEnabled
        })
        switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDarkTheme(isChecked)
        }

        viewModel.loadSettings()

        val shareButton: FrameLayout = view.findViewById(R.id.shareButton)
        shareButton.setOnClickListener {
            shareApp()
        }

        val supportButton: FrameLayout = view.findViewById(R.id.supportButton)
        supportButton.setOnClickListener {
            writeToSupport()
        }

        val agreementButton: FrameLayout = view.findViewById(R.id.agreementButton)
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
