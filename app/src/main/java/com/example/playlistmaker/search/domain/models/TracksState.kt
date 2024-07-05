package com.example.playlistmaker.search.domain.models

data class TracksState(
    val isLoading: Boolean = false,
    val isFailed: Boolean? = null,
    val tracks: List<Track> = emptyList()
)