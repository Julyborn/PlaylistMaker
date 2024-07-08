package com.example.playlistmaker.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.search.domain.Interfaces.TrackHistoryInteractor
import com.example.playlistmaker.search.domain.Interfaces.TrackInteractor

class SearchViewModelFactory(
    private val trackInteractor: TrackInteractor,
    private val trackHistoryInteractor: TrackHistoryInteractor
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SearchViewModel(trackInteractor, trackHistoryInteractor) as T
    }
}
