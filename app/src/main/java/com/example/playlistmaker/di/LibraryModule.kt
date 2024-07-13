package com.example.playlistmaker.di

import com.example.playlistmaker.library.presentation.LibraryFavoritesViewModel
import com.example.playlistmaker.library.presentation.LibraryPlaylistViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val libraryModule = module {

    viewModel {
        LibraryFavoritesViewModel()
    }

    viewModel {
        LibraryPlaylistViewModel()
    }
}