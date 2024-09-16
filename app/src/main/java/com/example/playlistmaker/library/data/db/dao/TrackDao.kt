package com.example.playlistmaker.library.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.library.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Query("DELETE FROM favorites WHERE trackId = :trackId")
    fun deleteTrack (trackId:Int)

    @Query("Select * FROM favorites ORDER BY number DESC")
    fun getAllTracks(): Flow<List<TrackEntity>>

    @Query("Select trackId FROM favorites")
    fun getAllTracksIDs(): Flow<List<Int>>
}