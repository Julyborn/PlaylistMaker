package com.example.playlistmaker.player.presentation


import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.PlayerInteractor
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val playerInteractor: PlayerInteractor) : ViewModel() {
    private val _playTime = MutableLiveData<String>()
    val playTime: LiveData<String> get() = _playTime

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    private val handler = Handler(Looper.getMainLooper())
    private val updatePlayTimeRunnable = object : Runnable {
        override fun run() {
            updatePlayTime()
            handler.postDelayed(this, 1000)
        }
    }

    init {
        playerInteractor.setOnPreparedListener {
            _isPlaying.value = false
        }
        playerInteractor.setOnCompletionListener {
            _isPlaying.value = false
            _playTime.value = "00:00"
            handler.removeCallbacks(updatePlayTimeRunnable)
            startPlayer()
        }
    }

    fun preparePlayer(url: String) {
        playerInteractor.preparePlayer(url)
    }

    fun startPlayer() {
        playerInteractor.startPlayer()
        _isPlaying.value = true
        handler.post(updatePlayTimeRunnable)
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        _isPlaying.value = false
        handler.removeCallbacks(updatePlayTimeRunnable)
    }

    fun releasePlayer() {
        playerInteractor.releasePlayer()
        handler.removeCallbacks(updatePlayTimeRunnable)
    }

    private fun updatePlayTime() {
        val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(playerInteractor.getCurrentPosition())
        _playTime.value = formattedTime
    }
}

