package com.example.playlistmaker.data

import android.icu.text.SimpleDateFormat
import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.data.dto.TrackSearchResponse
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track
import java.util.Locale

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {

    private val simpleDateFormatMmSs by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    override fun searchTracks(query: String): List<Track> {
        val response = networkClient.doRequest(TrackSearchRequest(query))
        if (response.resultCode == 200) {
            return (response as TrackSearchResponse).results.map {
                Track(
                    it.trackID,
                    it.trackName,
                    it.artistName,
                    simpleDateFormatMmSs.format(it.trackTimeMillis),
                    it.artworkUrl100,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl
                )
            }
        } else {
            return emptyList()
        }
    }
}