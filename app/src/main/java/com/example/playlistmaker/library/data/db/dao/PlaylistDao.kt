package com.example.playlistmaker.library.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.library.data.db.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Update(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Query("Select * FROM playlists ORDER BY id")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

    @Query("Select * FROM playlists WHERE id = :id")
    fun getPlaylist(id: Long): Flow<PlaylistEntity>

    @Query("DELETE FROM playlists WHERE id = :id")
    fun deletePlaylist(id: Long)
}