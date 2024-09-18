package com.example.playlistmaker.library.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.library.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.library.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: PlaylistTrackEntity)

    @Query("DELETE FROM playlist_tracks WHERE trackId = :trackId")
    fun deleteTrack (trackId:Int)

    @Query("Select * FROM playlist_tracks ORDER BY number DESC")
    fun getAllTracks(): Flow<List<PlaylistTrackEntity>>

    @Query("Select trackId FROM playlist_tracks")
    fun getAllTracksIDs(): Flow<List<Int>>

    @Query("Select * FROM playlist_tracks")
    fun getPlaylistTracks(): Flow<List<TrackEntity>>
}
