package com.example.playlistmaker.search.domain.interactors;


import com.example.playlistmaker.search.domain.Interfaces.TrackInteractor
import com.example.playlistmaker.search.domain.Interfaces.TrackRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {
    override fun searchTracks(expression: String): Flow<List<Track>> {
        return repository.searchTracks(expression)
    }
}