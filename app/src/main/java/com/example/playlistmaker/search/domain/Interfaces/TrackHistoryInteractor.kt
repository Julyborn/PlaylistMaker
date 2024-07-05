package com.example.playlistmaker.search.domain.Interfaces

import com.example.playlistmaker.search.domain.models.Track

interface TrackHistoryInteractor {
    fun loadHistory(): List<Track>
    fun addTrackToHistory(track: Track)
    fun clearHistory()
}