package com.example.playlistmaker.search.presentation


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.Interfaces.TrackHistoryInteractor
import com.example.playlistmaker.search.domain.Interfaces.TrackInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.models.TracksState

class SearchViewModel(
    private val trackInteractor: TrackInteractor,
    private val trackHistoryInteractor: TrackHistoryInteractor
) : ViewModel() {

    private val _tracksState = MutableLiveData<TracksState>()
    val tracksState: LiveData<TracksState> get() = _tracksState

    private val _historyList = MutableLiveData<List<Track>>()
    val historyList: LiveData<List<Track>> get() = _historyList

    private val handler = android.os.Handler(android.os.Looper.getMainLooper())

    fun searchTracks(query: String) {
        val tracks = mutableListOf<Track>()
        if (query.isNotEmpty()) {
            _tracksState.postValue(
                TracksState(
                    tracks = emptyList(),
                    isLoading = true,
                    isFailed = null
                )
            )

            trackInteractor.searchTracks(query, object : TrackInteractor.TrackConsumer {
                override fun consume(foundTracks: List<Track>) {
                    handler.post {
                        tracks.clear()
                        tracks.addAll(foundTracks)
                        _tracksState.postValue(
                            TracksState(
                                tracks = tracks,
                                isLoading = false,
                                isFailed = null
                            )
                        )
                    }
                }

                override fun onError(e: Exception) {
                    Log.e("SearchViewModel", "Error during searchTracks: ${e.message}")
                    handler.post {
                        _tracksState.postValue(
                            TracksState(
                                tracks = emptyList(),
                                isLoading = false,
                                isFailed = true
                            )
                        )
                    }
                }
            })
        }
    }

    fun loadSearchHistory() {
        _historyList.value = trackHistoryInteractor.loadHistory()
    }

    fun addTrackToHistory(track: Track) {
        trackHistoryInteractor.addTrackToHistory(track)
        loadSearchHistory()
    }

    fun clearSearchHistory() {
        trackHistoryInteractor.clearHistory()
        loadSearchHistory()
    }
}