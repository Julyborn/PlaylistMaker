package com.example.playlistmaker.library.domain.playlist

import android.content.Context
import android.net.Uri
import com.example.playlistmaker.library.data.db.entity.PlaylistEntity
import com.example.playlistmaker.library.data.db.entity.PlaylistTrackEntity
import kotlinx.coroutines.flow.Flow
import java.io.InputStream
import java.util.Date

interface PlaylistRepository {
    fun getPlaylists(): Flow<List<PlaylistEntity>>
    suspend fun insertPlaylists(playlist: PlaylistEntity)
    suspend fun updatePlaylist(playlist: PlaylistEntity)
    fun saveImage(context: Context, name: String, inputStream: InputStream?, time: Date): Uri
    suspend fun savePlaylistTrack(track: PlaylistTrackEntity)
}