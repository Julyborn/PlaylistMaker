package com.example.playlistmaker.library.domain.favorite

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteInteractor {
    fun getAllTracks(): Flow<List<Track>>
    suspend fun addTrack(track: Track)
    suspend fun removeTrack(track: Track)
    fun getAllTrackIDs(): Flow<List<Int>>
}