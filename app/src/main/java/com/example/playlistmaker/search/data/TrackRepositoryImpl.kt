package com.example.playlistmaker.search.data

import android.icu.text.SimpleDateFormat
import android.util.Log
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.dto.TrackSearchResponse
import com.example.playlistmaker.search.data.dto.network.NetworkClient
import com.example.playlistmaker.search.domain.Interfaces.TrackRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.io.IOException
import java.util.Locale

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {

    fun getTimeFormat(value: Long): String =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(value)
    override fun searchTracks(query: String): Flow<List<Track>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(query))
        if (response.resultCode == 200) {
            val trackResponse = response as? TrackSearchResponse
            trackResponse?.let {
                emit(it.results.map { trackDto ->
                    Log.d("БД", "Трек в поиске: название ${trackDto.trackName}\" id ${trackDto.trackId}")
                    Track(
                        trackDto.trackId,
                        trackDto.trackName,
                        trackDto.artistName,
                        getTimeFormat(trackDto.trackTime.toLong()),
                        trackDto.artworkUrl,
                        trackDto.collectionName,
                        trackDto.releaseDate,
                        trackDto.primaryGenreName,
                        trackDto.country,
                        trackDto.previewUrl
                    )

                })
            } ?: Log.e("TrackRepositoryImpl", "Response casting error: expected TrackSearchResponse")
        } else {
            throw IOException("Response error: ${response.resultCode}")
        }
    }.catch { e ->
        throw e
    }
}