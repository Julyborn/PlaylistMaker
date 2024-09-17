package com.example.playlistmaker.player.presentation


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.favorite.FavoriteInteractor
import com.example.playlistmaker.library.domain.playlist.Playlist
import com.example.playlistmaker.library.domain.playlist.PlaylistInteractor
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favoriteInteractor: FavoriteInteractor,
    private val playlistInteractor: PlaylistInteractor

) : ViewModel() {

    private val _playTime = MutableLiveData<String>()
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private var timerJob: Job? = null
    private val _trackLiveData = MutableLiveData<Track>()
    private val favoriteTracksLiveData: LiveData<List<Int>> = favoriteInteractor.getAllTrackIDs().asLiveData()
    private var playlistList: List<Playlist> = listOf()
    private val _playlistsLiveData = MutableLiveData<List<Playlist>>()
    val playlistsLiveData: LiveData<List<Playlist>> get() = _playlistsLiveData

    val playTime: LiveData<String> get() = _playTime

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> get() = _isPlaying
    private fun startProgressUpdate() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_isPlaying.value == true) {
                updatePlayTime()
                delay(300)
            }
        }
    }


    init {
        playerInteractor.setOnPreparedListener {
            _isPlaying.value = false
        }
        playerInteractor.setOnCompletionListener {
            _isPlaying.value = false
            _playTime.value = dateFormat.format(0)
        }
    }

    fun preparePlayer(url: String) {
        playerInteractor.preparePlayer(url)
    }

    fun startPlayer() {
        playerInteractor.startPlayer()
        _isPlaying.value = true
        startProgressUpdate()
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        _isPlaying.value = false
    }

    fun releasePlayer() {
        playerInteractor.releasePlayer()
        startProgressUpdate()
    }

    private fun updatePlayTime() {
        _playTime.value = dateFormat.format(playerInteractor.getCurrentPosition())
    }

    fun favButClicked(track: Track) {
        viewModelScope.launch {
            val isFavorite = favoriteTracksLiveData.value?.contains(track.trackID) ?: false
            if (isFavorite) {
                favoriteInteractor.removeTrack(track)
            } else {
                favoriteInteractor.addTrack(track)
            }
            _trackLiveData.value = track
        }
    }


    fun isTrackFavoriteLiveData(trackId: Int): LiveData<Boolean> {
        return favoriteTracksLiveData.map { favoriteTrackIds ->
            favoriteTrackIds.contains(trackId)
        }
    }

    fun getPlaylists() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.getPlaylists().collect { playlists ->
                _playlistsLiveData.postValue(playlists)
            }
        }
    }

    fun updatePlaylists(playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) { playlistInteractor.updatePlaylist(playlist) }
    }
    fun insertPlaylistTrack(track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.insertPlaylistTrack(track)
        }
    }
}

