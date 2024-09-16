package com.example.playlistmaker.library.domain.playlist

data class Playlist(
    val name: String,
    val image: String?,
    val count: Int = 0,
    val info: String?,
    val content: String,
    val id: Long = 0

)