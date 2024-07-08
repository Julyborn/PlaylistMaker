package com.example.playlistmaker.search.data.dto

import com.google.gson.annotations.SerializedName

data class TrackDto(
    val trackID: Int,
    val trackName: String,
    val artistName: String,
    @SerializedName("trackTimeMillis") val trackTime: String,
    @SerializedName("artworkUrl100") val artworkUrl: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
)