package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.models.Track

interface TrackInteractionListener {
    fun onTrackSelected(track: Track)
}