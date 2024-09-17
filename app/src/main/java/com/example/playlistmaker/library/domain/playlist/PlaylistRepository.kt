package com.example.playlistmaker.library.domain.playlist

import android.net.Uri
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import java.io.InputStream
import java.util.Date


interface PlaylistRepository {
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun insertPlaylists(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    fun saveImage(name: String, inputStream: InputStream?, time: Date): Uri
    suspend fun savePlaylistTrack(track: Track)
}