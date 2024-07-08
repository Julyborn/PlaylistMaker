package com.example.playlistmaker.search.domain.interactors;


import com.example.playlistmaker.search.domain.Interfaces.TrackInteractor
import com.example.playlistmaker.search.domain.Interfaces.TrackRepository
import java.util.concurrent.Executors

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(query: String, consumer: TrackInteractor.TrackConsumer) {
        executor.execute {
            try {
                val tracks = repository.searchTracks(query)
                consumer.consume(tracks)
            } catch (e: Exception) {
                consumer.onError(e)
            }
        }
    }
}