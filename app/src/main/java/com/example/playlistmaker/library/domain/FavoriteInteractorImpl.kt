package com.example.playlistmaker.library.domain

import com.example.playlistmaker.library.data.db.TrackEntity
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteInteractorImpl(
    private val favoriteRepository: FavoriteRepository
) : FavoriteInteractor {

    override fun getAllTracks(): Flow<List<Track>> {
        return favoriteRepository.getAllTracks().map { trackEntities ->
            trackEntities.map { entity -> entity.toDomainModel() }
        }
    }

    override suspend fun addTrack(track: Track) {
        favoriteRepository.addTrack(track.toEntity())
    }

    override suspend fun removeTrack(track: Track) {
        favoriteRepository.removeTrack(track.toEntity())
    }

    override fun getAllTrackIDs(): Flow<List<Int>> {
        return favoriteRepository.getAllTracksIDs()
    }

    private fun TrackEntity.toDomainModel(): Track {
        return Track(
            trackID = this.trackId,
            trackName = this.trackName,
            artistName = this.artistName,
            trackTime = this.trackTime,
            artworkUrl = this.artworkUrl,
            collectionName = this.collectionName,
            releaseDate = this.releaseDate,
            primaryGenreName = this.primaryGenreName,
            country = this.country,
            previewUrl = this.trackUrl,
        )
    }

    private fun Track.toEntity(): TrackEntity {
        return TrackEntity(
            trackId = this.trackID,
            trackName = this.trackName,
            artistName = this.artistName,
            trackTime = this.trackTime,
            artworkUrl = this.artworkUrl,
            collectionName = this.collectionName,
            releaseDate = this.releaseDate,
            primaryGenreName = this.primaryGenreName,
            country = this.country,
            trackUrl = this.previewUrl
        )
    }
}
