package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TrackInteractionListener {
    fun onTrackSelected(track: Track)
}