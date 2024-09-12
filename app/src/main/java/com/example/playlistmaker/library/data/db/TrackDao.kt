package com.example.playlistmaker.library.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Query("DELETE FROM favorite WHERE trackId = :trackId")
    fun deleteTrack (trackId:Int)

    @Query("Select * FROM favorite ORDER BY number DESC")
    fun getAllTracks(): Flow<List<TrackEntity>>

    @Query("Select trackId FROM favorite")
    fun getAllTracksIDs(): List<Int>
}