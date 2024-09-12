package com.example.playlistmaker.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.Interfaces.TrackHistoryInteractor
import com.example.playlistmaker.search.domain.Interfaces.TrackInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.models.TracksState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class SearchViewModel(
    private val trackInteractor: TrackInteractor,
    private val trackHistoryInteractor: TrackHistoryInteractor
) : ViewModel() {

    private val _tracksState = MutableStateFlow<TracksState>(TracksState.Empty)
    val tracksState: StateFlow<TracksState> get() = _tracksState

    private val _historyList = MutableStateFlow<List<Track>>(emptyList())
    val historyList: StateFlow<List<Track>> get() = _historyList

    private var searchJob: Job? = null
    private var lastRequest: String? = null
    private val SEARCH_DEBOUNCE_DELAY = 2000L

    fun searchTracks(query: String) {
        if (query.isEmpty() || query == lastRequest) {
            return
        }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _tracksState.value = TracksState.Loading
            lastRequest = query
            trackInteractor.searchTracks(query)
                .onEach { tracks ->
                    _tracksState.value = if (tracks.isNotEmpty()) {
                        TracksState.Success(tracks)
                    } else {
                        TracksState.Empty
                    }
                }
                .catch { e ->
                    if (e !is CancellationException) {
                        val isNetworkError = e is java.io.IOException
                        _tracksState.value = TracksState.Error(isNetworkError)
                    }
                }
                .launchIn(this)
        }
    }

    fun searchDebounce(newRequest: String) {
        if (newRequest == lastRequest) return

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchTracks(newRequest)
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
