package com.example.playlistmaker.library.presentation

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.playlist.Playlist
import com.example.playlistmaker.library.domain.playlist.PlaylistInteractor
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import java.util.Date

class PlaylistCreatorViewModel(private val interactor: PlaylistInteractor) : ViewModel() {

    private val fileLiveData = MutableLiveData<Uri>()
    private val playlistLiveData = MutableLiveData<Playlist>()

    private lateinit var currentPlaylist: Playlist

    fun getFile(): LiveData<Uri> = fileLiveData

    fun getPlaylistLiveData(): LiveData<Playlist> = playlistLiveData

    fun updatePlaylists(playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.insertPlaylists(playlist)
        }
    }

    fun getPlaylist(intent: Intent) {
        val playlistText = intent.extras?.getString("playlist") ?: ""
        currentPlaylist = if (playlistText.isEmpty()) {
            Playlist("", "", 0, "", "", 0L)
        } else {
            Gson().fromJson(playlistText, Playlist::class.java)
        }
        playlistLiveData.postValue(currentPlaylist)
    }

    fun saveImage(name: String, inputStream: InputStream?, time: Date) {
        val uri = interactor.saveImage(name, inputStream, time)
        fileLiveData.postValue(uri)
    }
}
