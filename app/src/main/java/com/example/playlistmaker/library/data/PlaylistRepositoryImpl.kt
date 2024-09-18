package com.example.playlistmaker.library.data

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import com.example.playlistmaker.library.data.db.dao.PlaylistDao
import com.example.playlistmaker.library.data.db.dao.PlaylistTrackDao
import com.example.playlistmaker.library.domain.playlist.Playlist
import com.example.playlistmaker.library.domain.playlist.PlaylistRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Date

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val application: Application
) : PlaylistRepository {

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getPlaylists().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun insertPlaylists(playlist: Playlist) {
        playlistDao.insertPlaylist(playlist.toEntity())
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(playlist.toEntity())
    }

    override suspend fun savePlaylistTrack(track: Track) {
        playlistTrackDao.insertTrack(track.toPlaylistTrackEntity())
    }

    override fun getPlaylist(id: Long): Flow<Playlist> {
        return playlistDao.getPlaylist(id)
            .map { playlistEntity ->
                playlistEntity?.toDomainModel() ?: Playlist(id = 0, name = "", image = "", count = 0, info = "", content = "")
            }
    }

    override fun getPlaylistTracks(): Flow<List<Track>> {
        return playlistTrackDao.getPlaylistTracks()
            .map { playlistTrackEntities ->
                playlistTrackEntities.map { playlistTrackEntity ->
                    playlistTrackEntity.toDomainModel()
                }
            }
    }

    override suspend fun deleteTrack(playlist: Playlist, trackId: Int) {
        val updatedPlaylistContent = playlist.content
            .split(",")
            .filter { it.isNotBlank() }
            .filterNot { it.toInt() == trackId }
            .joinToString(",")

        val updatedPlaylist = playlist.copy(content = updatedPlaylistContent, count = playlist.count - 1)
        playlistDao.updatePlaylist(updatedPlaylist.toEntity())

        val trackUsageCount = playlistTrackDao.getTrackInPlaylistsCount(trackId)

        if (trackUsageCount == 0) {
            playlistTrackDao.deleteTrack(trackId)
        }
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistDao.deletePlaylist(playlist.id)
        val playlistTracks = getPlaylistTracks().first()
        playlistTracks.forEach { track ->
            deleteTrack(playlist, track.trackID)
        }
    }

    override fun saveImage(
        name: String,
        inputStream: InputStream?,
        time: Date
    ): Uri {
        val filePath = File(application.getExternalFilesDir(Environment.DIRECTORY_PICTURES), name)
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, "${time}_playlist_image.jpg")
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return file.toUri()
    }


}