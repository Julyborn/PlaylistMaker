package com.example.playlistmaker.library.domain.playlist

import android.content.Context
import android.net.Uri
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import java.io.InputStream
import java.util.Date

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
) : PlaylistInteractor {

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }

    override suspend fun insertPlaylists(playlist: Playlist) {
        playlistRepository.insertPlaylists(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistRepository.updatePlaylist(playlist)
    }

    override fun saveImage(
        name: String,
        inputStream: InputStream?,
        time: Date
    ): Uri {
        return playlistRepository.saveImage(name, inputStream, time)
    }

    override suspend fun insertPlaylistTrack(track: Track) {
        playlistRepository.savePlaylistTrack(track)
    }
}