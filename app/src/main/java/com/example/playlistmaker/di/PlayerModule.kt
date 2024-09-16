package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.domain.PlayerInteractorImpl
import com.example.playlistmaker.player.domain.PlayerRepository
import com.example.playlistmaker.player.presentation.PlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {
    factory  { MediaPlayer() }

    factory <PlayerRepository> {
        PlayerRepositoryImpl(mediaPlayer = get())
    }

    factory <PlayerInteractor> {
        PlayerInteractorImpl(repository = get())
    }

    viewModel {
        PlayerViewModel(playerInteractor = get(), favoriteInteractor = get(), playlistInteractor = get())
    }
}