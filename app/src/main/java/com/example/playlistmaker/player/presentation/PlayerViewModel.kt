package com.example.playlistmaker.player.presentation


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.PlayerInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor
) : ViewModel() {

    private val _playTime = MutableLiveData<String>()
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private var timerJob: Job? = null

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
            startPlayer()
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
}

