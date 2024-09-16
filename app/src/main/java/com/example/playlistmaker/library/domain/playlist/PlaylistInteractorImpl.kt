package com.example.playlistmaker.library.domain.playlist

import android.content.Context
import android.net.Uri
import com.example.playlistmaker.library.data.db.entity.PlaylistEntity
import com.example.playlistmaker.library.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.InputStream
import java.util.Date

class PlaylistInteractorImpl (private val playlistRepository: PlaylistRepository) : PlaylistInteractor {
    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists().map { playlistEntities ->
            playlistEntities.map { it.toDomainModel() }
        }
    }
    override suspend fun insertPlaylists(playlist: Playlist) {
        playlistRepository.insertPlaylists(playlist.toEntity())
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistRepository.updatePlaylist(playlist.toEntity())
    }

    override fun saveImage(
        context: Context,
        name: String,
        inputStream: InputStream?,
        time: Date
    ): Uri {
        return playlistRepository.saveImage(context, name, inputStream, time)
    }

    override suspend fun insertPlaylistTrack(track: Track) {
        playlistRepository.savePlaylistTrack(track.toEntity())
    }



    private fun PlaylistEntity.toDomainModel(): Playlist{
        return Playlist(
            id = this.id,
            name = this.name,
            image = this.image,
            count = this.count,
            info = this.info,
            content = this.content
        )
    }

    private fun Playlist.toEntity(): PlaylistEntity{
        return PlaylistEntity(
            id = this.id,
            name = this.name,
            image = this.image,
            count = this.count,
            info = this.info,
            content = this.content
        )
    }

    private fun Track.toEntity(): PlaylistTrackEntity {
        return PlaylistTrackEntity(
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