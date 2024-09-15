package com.example.playlistmaker.library.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.playlistmaker.library.domain.FavoriteInteractor
import com.example.playlistmaker.search.domain.models.Track

class LibraryFavoritesViewModel(private val favoriteInteractor: FavoriteInteractor) : ViewModel() {

    val tracksLiveData: LiveData<List<Track>> = favoriteInteractor.getAllTracks().asLiveData()


}
