package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.models.Track

interface TrackRepository {
    fun searchTracks(searchText: String): List<Track>
}