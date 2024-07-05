package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.search.data.TrackRepository
import com.example.playlistmaker.search.data.TrackRepositoryImpl
import com.example.playlistmaker.search.data.dto.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.TrackInteractor
import com.example.playlistmaker.search.domain.TrackInteractorImpl
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.SettingsInteractorImpl

object Creator {
    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        val settingsRepository = SettingsRepositoryImpl(context)
        return SettingsInteractorImpl(settingsRepository)
    }


}
