package com.example.playlistmaker.search.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.search.data.TrackHistoryRepository
import com.example.playlistmaker.search.domain.TrackInteractor

    class SearchViewModelFactory(
        private val context: Context,
        private val trackInteractor: TrackInteractor,
        private val trackHistoryRepository: TrackHistoryRepository
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                return SearchViewModel(context, trackInteractor, trackHistoryRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }