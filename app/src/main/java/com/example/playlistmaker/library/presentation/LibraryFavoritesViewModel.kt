package com.example.playlistmaker.library.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.playlistmaker.library.data.db.TrackEntity
import com.example.playlistmaker.library.domain.FavoriteRepository
class LibraryFavoritesViewModel(private val repository: FavoriteRepository) : ViewModel() {

    val tracksLiveData: LiveData<List<TrackEntity>> = repository.getAllTracks().asLiveData()


}
