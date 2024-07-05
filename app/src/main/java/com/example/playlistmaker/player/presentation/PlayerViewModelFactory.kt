package com.example.playlistmaker.player.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.player.domain.PlayerInteractor

class PlayerViewModelFactory(private val playerInteractor: PlayerInteractor) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayerViewModel(playerInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
