package com.example.playlistmaker.creator

import android.app.Application
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.domain.PlayerInteractorImpl
import com.example.playlistmaker.player.domain.PlayerRepository
import com.example.playlistmaker.search.data.TrackHistoryRepositoryImpl
import com.example.playlistmaker.search.data.TrackRepositoryImpl
import com.example.playlistmaker.search.data.dto.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.Interfaces.TrackHistoryInteractor
import com.example.playlistmaker.search.domain.Interfaces.TrackHistoryRepository
import com.example.playlistmaker.search.domain.Interfaces.TrackInteractor
import com.example.playlistmaker.search.domain.Interfaces.TrackRepository
import com.example.playlistmaker.search.domain.interactors.TrackHistoryInteractorImpl
import com.example.playlistmaker.search.domain.interactors.TrackInteractorImpl
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.SettingsInteractorImpl
import com.example.playlistmaker.settings.domain.SettingsRepository

object Creator {
    lateinit var application: Application

    fun initApplication(app: Application){
        application = app
    }
    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }
    private fun getHistoryTrackRepository(): TrackHistoryRepository {
        return TrackHistoryRepositoryImpl()
    }
    fun provideTrackHistoryInteractor(): TrackHistoryInteractor {
        return TrackHistoryInteractorImpl(getHistoryTrackRepository())
    }
    private fun getSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl()
    }
    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository())
    }
    private fun getPlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl()
    }
    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository())
    }
}
