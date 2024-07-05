package com.example.playlistmaker.search.data

import android.icu.text.SimpleDateFormat
import android.util.Log
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.dto.TrackSearchResponse
import com.example.playlistmaker.search.data.dto.network.NetworkClient
import com.example.playlistmaker.search.domain.models.Track
import java.util.Locale

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {

    fun getTimeFormat(value: Long): String =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(value)
    override fun searchTracks(query: String): List<Track> {
        val response = networkClient.doRequest(TrackSearchRequest(query))
        if (response.resultCode == 200) {
            return (response as TrackSearchResponse).results.map {
                Track(
                    it.trackID,
                    it.trackName,
                    it.artistName,
                    getTimeFormat(it.trackTime.toLong()),
                    it.artworkUrl,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl
                )
            }
        } else {
            Log.e("TrackRepositoryImpl", "Response error: ${response.resultCode}")
            return emptyList()
        }
    }
}