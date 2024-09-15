package com.example.playlistmaker.library.domain;

import com.example.playlistmaker.library.data.db.TrackEntity
import kotlinx.coroutines.flow.Flow

interface  FavoriteRepository {
    suspend fun addTrack(track:TrackEntity)
    suspend fun removeTrack(track: TrackEntity)
    fun getAllTracks(): Flow<List<TrackEntity>>
    fun getAllTracksIDs(): Flow<List<Int>>
}
