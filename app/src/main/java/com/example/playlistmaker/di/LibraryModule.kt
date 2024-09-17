package com.example.playlistmaker.di

import FavoriteRepositoryImpl
import com.example.playlistmaker.library.data.PlaylistRepositoryImpl
import com.example.playlistmaker.library.domain.favorite.FavoriteInteractor
import com.example.playlistmaker.library.domain.favorite.FavoriteInteractorImpl
import com.example.playlistmaker.library.domain.favorite.FavoriteRepository
import com.example.playlistmaker.library.domain.playlist.PlaylistInteractor
import com.example.playlistmaker.library.domain.playlist.PlaylistInteractorImpl
import com.example.playlistmaker.library.domain.playlist.PlaylistRepository
import com.example.playlistmaker.library.presentation.LibraryFavoritesViewModel
import com.example.playlistmaker.library.presentation.LibraryPlaylistViewModel
import com.example.playlistmaker.library.presentation.PlaylistCreatorViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val libraryModule = module {

    viewModel {
        LibraryFavoritesViewModel(get())
    }

    factory<FavoriteInteractor> {
        FavoriteInteractorImpl(favoriteRepository = get())
    }

    factory<FavoriteRepository> {
        FavoriteRepositoryImpl(trackDao = get())
    }

    viewModel {
        LibraryPlaylistViewModel(get())
    }

    factory<PlaylistInteractor> {
        PlaylistInteractorImpl(playlistRepository = get())
    }

    factory<PlaylistRepository> {
        PlaylistRepositoryImpl(
            playlistDao = get(),
            playlistTrackDao = get(),
            application = androidApplication()
        )
    }

    viewModel {
        PlaylistCreatorViewModel(get())
    }
}