package com.example.playlistmaker.library.data.di

import FavoriteRepositoryImpl
import com.example.playlistmaker.library.domain.favorite.FavoriteRepository
import org.koin.dsl.module

val LibraryModule = module {
    single<FavoriteRepository> { FavoriteRepositoryImpl(get()) }
}