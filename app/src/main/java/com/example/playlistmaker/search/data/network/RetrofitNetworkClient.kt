package com.example.playlistmaker.search.data.dto.network

import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TrackSearchRequest

class RetrofitNetworkClient(private val iTunes: ITunesAPI): NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        return try {
            if (dto is TrackSearchRequest) {
                val response = iTunes.search(dto.searchText)
                response.resultCode = 200
                response
            } else {
                Response(400)
            }
        } catch (e: Exception) {
            Response(500)
        }
    }
}