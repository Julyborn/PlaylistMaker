package com.example.playlistmaker.library.data.db


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite")
data class TrackEntity(
    @PrimaryKey(autoGenerate = true)
    val number: Int = 0,
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val trackUrl: String,
)