package com.example.playlistmaker.search.domain.models

sealed class TracksState {
    object Loading : TracksState()
    data class Success(val tracks: List<Track>) : TracksState()
    data class Error(val isNetworkError: Boolean) : TracksState()
    object Empty : TracksState()
}