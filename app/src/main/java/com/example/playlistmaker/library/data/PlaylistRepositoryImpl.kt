package com.example.playlistmaker.library.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import com.example.playlistmaker.library.data.db.dao.PlaylistDao
import com.example.playlistmaker.library.data.db.dao.PlaylistTrackDao
import com.example.playlistmaker.library.data.db.entity.PlaylistEntity
import com.example.playlistmaker.library.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.library.domain.playlist.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Date

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao
) : PlaylistRepository {
    override fun getPlaylists(): Flow<List<PlaylistEntity>> {
        return playlistDao.getPlaylists()
    }

    override suspend fun insertPlaylists(playlist: PlaylistEntity) {
        playlistDao.insertPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: PlaylistEntity) {
        playlistDao.updatePlaylist(playlist)
    }

    override suspend fun savePlaylistTrack(track: PlaylistTrackEntity) {
        playlistTrackDao.insertTrack(track)
    }

    override fun saveImage(
        context: Context,
        name: String,
        inputStream: InputStream?,
        time: Date
    ): Uri {
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), name)
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

