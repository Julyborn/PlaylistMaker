package com.example.playlistmaker.library.domain.favorite;

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    suspend fun addTrack(track: Track)
    suspend fun removeTrack(track: Track)
    fun getAllTracks(): Flow<List<Track>>
    fun getAllTrackIDs(): Flow<List<Int>>
}