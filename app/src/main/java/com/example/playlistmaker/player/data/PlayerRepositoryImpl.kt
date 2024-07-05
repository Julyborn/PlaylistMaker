package com.example.playlistmaker.player.data

import android.media.MediaPlayer

class PlayerRepositoryImpl : PlayerRepository {
    private val mediaPlayer = MediaPlayer()

    override fun preparePlayer(url: String) {
        mediaPlayer.apply {
            setDataSource(url)
            prepareAsync()
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun setOnPreparedListener(listener: () -> Unit) {
        mediaPlayer.setOnPreparedListener { listener() }
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        mediaPlayer.setOnCompletionListener { listener() }
    }
}
