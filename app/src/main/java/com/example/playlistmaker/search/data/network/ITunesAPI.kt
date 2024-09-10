package com.example.playlistmaker.search.data.dto.network
import com.example.playlistmaker.search.data.dto.TrackSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesAPI {
    @GET("/search?entity=song ")
    suspend fun search(@Query("term") text: String): TrackSearchResponse

}