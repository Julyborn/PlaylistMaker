package com.example.playlistmaker.search.data.dto.network

import android.content.Context
import android.util.Log
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TrackSearchRequest

class RetrofitNetworkClient(private val context: Context, private val iTunes: ITunesAPI): NetworkClient {

    override fun doRequest(dto: Any): Response {
        return if (dto is TrackSearchRequest) {
            val response = iTunes.search(dto.searchText).execute()
            val body = response.body() ?: Response()
            body.apply { resultCode = response.code() }
            Log.d("RetrofitNetworkClient", "Request successful: ${response.code()}")
            return body

        } else {
            Log.e("RetrofitNetworkClient", "Invalid request object: $dto")
            Response().apply { resultCode = 400 }
        }
    }
}