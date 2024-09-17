package com.example.playlistmaker.library.domain.favorite

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoriteInteractorImpl(
    private val favoriteRepository: FavoriteRepository
) : FavoriteInteractor {


    override fun getAllTracks(): Flow<List<Track>> {
        return favoriteRepository.getAllTracks()
    }

    override suspend fun addTrack(track: Track) {
        favoriteRepository.addTrack(track)
    }

    override suspend fun removeTrack(track: Track) {
        favoriteRepository.removeTrack(track)
    }

    override fun getAllTrackIDs(): Flow<List<Int>> {
        return favoriteRepository.getAllTrackIDs()
    }
}

