package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TrackInteractor  {
    fun searchTracks(query: String, consumer: TrackConsumer)

    interface TrackConsumer  {
        fun consume(foundTracks: List<Track>)
        fun onError(e: Exception)

    }
}