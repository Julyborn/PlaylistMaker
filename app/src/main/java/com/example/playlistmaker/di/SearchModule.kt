package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.App.Companion.APPLICATION_PREFERENCES
import com.example.playlistmaker.App.Companion.BASE_URL
import com.example.playlistmaker.search.data.TrackHistoryRepositoryImpl
import com.example.playlistmaker.search.data.TrackRepositoryImpl
import com.example.playlistmaker.search.data.dto.network.ITunesAPI
import com.example.playlistmaker.search.data.dto.network.NetworkClient
import com.example.playlistmaker.search.data.dto.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.Interfaces.TrackHistoryInteractor
import com.example.playlistmaker.search.domain.Interfaces.TrackHistoryRepository
import com.example.playlistmaker.search.domain.Interfaces.TrackInteractor
import com.example.playlistmaker.search.domain.Interfaces.TrackRepository
import com.example.playlistmaker.search.domain.interactors.TrackHistoryInteractorImpl
import com.example.playlistmaker.search.domain.interactors.TrackInteractorImpl
import com.example.playlistmaker.search.presentation.SearchViewModel
import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val searchModule = module {
    single {
        androidContext().getSharedPreferences(APPLICATION_PREFERENCES, Context.MODE_PRIVATE)
    }

    single { Gson() }

    single<ITunesAPI> {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesAPI::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(context = androidContext(), iTunes = get())
    }

    single<TrackHistoryRepository> {
        TrackHistoryRepositoryImpl()
    }

    single<TrackRepository> {
        TrackRepositoryImpl(networkClient = get())
    }

    single<TrackHistoryInteractor> {
        TrackHistoryInteractorImpl(trackHistoryRepository = get())
    }

    single<TrackInteractor> {
        TrackInteractorImpl(repository = get())
    }

    viewModel {
        SearchViewModel(trackInteractor = get(), trackHistoryInteractor = get())
    }
}