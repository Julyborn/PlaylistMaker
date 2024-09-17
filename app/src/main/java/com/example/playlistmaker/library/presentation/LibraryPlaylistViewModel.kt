package com.example.playlistmaker.library.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.playlist.Playlist
import com.example.playlistmaker.library.domain.playlist.PlaylistInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LibraryPlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val playlistsLiveData = MutableLiveData<List<Playlist>>()
    fun returnPlaylists(): LiveData<List<Playlist>> = playlistsLiveData

    fun getPlaylists() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.getPlaylists().collect { playlists ->
                bind(playlists)
            }
        }
    }

    private fun bind(playlists: List<Playlist>) {
        playlistsLiveData.postValue(playlists)
    }
}