package com.example.playlistmaker.library.data.di

import FavoriteRepositoryImpl
import com.example.playlistmaker.library.domain.FavoriteRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<FavoriteRepository> { FavoriteRepositoryImpl(get()) }
}