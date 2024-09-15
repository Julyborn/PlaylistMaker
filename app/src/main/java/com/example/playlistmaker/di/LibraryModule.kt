package com.example.playlistmaker.di

import FavoriteRepositoryImpl
import com.example.playlistmaker.library.domain.FavoriteInteractor
import com.example.playlistmaker.library.domain.FavoriteInteractorImpl
import com.example.playlistmaker.library.domain.FavoriteRepository
import com.example.playlistmaker.library.presentation.LibraryFavoritesViewModel
import com.example.playlistmaker.library.presentation.LibraryPlaylistViewModel
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
        LibraryPlaylistViewModel()
    }
}