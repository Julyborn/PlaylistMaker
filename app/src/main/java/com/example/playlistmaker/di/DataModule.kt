package com.example.playlistmaker.library.data.di

import android.app.Application
import androidx.room.Room
import com.example.playlistmaker.library.data.db.AppDatabase
import org.koin.dsl.module

val dataModule = module {

    single {
        Room.databaseBuilder(
            get<Application>(),
            AppDatabase::class.java, "app-database"
        ).build()
    }

        single { get<AppDatabase>().trackDao() }
}