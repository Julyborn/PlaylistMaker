package com.example.playlistmaker.search.domain.Interfaces

import com.example.playlistmaker.search.domain.models.Track

interface TrackInteractor {
    fun searchTracks(query: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(foundTracks: List<Track>)
        fun onError(e: Exception)
    }
}