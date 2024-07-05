package com.example.playlistmaker.search.data.dto.network

import android.util.Log
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val baseUrl = "https://itunes.apple.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunes = retrofit.create(ITunesAPI::class.java)

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