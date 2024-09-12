package com.example.playlistmaker.player.presentation


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.data.db.TrackEntity
import com.example.playlistmaker.library.domain.FavoriteRepository
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favoriteRepository: FavoriteRepository

) : ViewModel() {

    private val _playTime = MutableLiveData<String>()
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private var timerJob: Job? = null
    private val _trackLiveData = MutableLiveData<Track>()
    val trackLiveData: LiveData<Track> get() = _trackLiveData

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

    fun FavButClicked(track: Track) {
        viewModelScope.launch {
            track.isFavorite = !track.isFavorite
            if (track.isFavorite) {
                favoriteRepository.addTrack(track.toEntity())
            } else {
                favoriteRepository.removeTrack(track.toEntity())
            }
            _trackLiveData.value = track
        }
    }
    private fun Track.toEntity(): TrackEntity {
        return TrackEntity(
            trackId = this.trackID,
            trackName = this.trackName,
            artistName = this.artistName,
            trackTime = this.trackTime,
            artworkUrl = this.artworkUrl,
            collectionName = this.collectionName,
            releaseDate = this.releaseDate,
            primaryGenreName = this.primaryGenreName,
            country = this.country,
            trackUrl = this.previewUrl
        )
    }

    suspend fun isTrackFavorite(trackId: Int): Boolean {
        return withContext(Dispatchers.IO) {
            val favoriteTrackIds = favoriteRepository.getAllTracksIDs()
            favoriteTrackIds.contains(trackId)
        }
    }
}

