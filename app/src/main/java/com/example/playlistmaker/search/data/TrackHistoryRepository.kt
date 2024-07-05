package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.models.Track

interface TrackHistoryRepository {
    fun loadHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
}