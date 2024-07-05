package com.example.playlistmaker.search.domain.interactors

import com.example.playlistmaker.search.domain.Interfaces.TrackHistoryInteractor
import com.example.playlistmaker.search.domain.Interfaces.TrackHistoryRepository
import com.example.playlistmaker.search.domain.models.Track

class TrackHistoryInteractorImpl(private val trackHistoryRepository: TrackHistoryRepository) : TrackHistoryInteractor {
    override fun loadHistory(): List<Track> {
        return trackHistoryRepository.loadHistory()
    }

    override fun addTrackToHistory(track: Track) {
        trackHistoryRepository.addTrack(track)
    }

    override fun clearHistory() {
        trackHistoryRepository.clearHistory()
    }
}