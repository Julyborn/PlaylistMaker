package com.example.playlistmaker.library.presentation

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.playlist.Playlist
import com.example.playlistmaker.library.domain.playlist.PlaylistInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import java.util.Date

class PlaylistCreatorViewModel(private val interactor: PlaylistInteractor) : ViewModel() {
    private val fileLiveData = MutableLiveData<Uri>()

    fun getFile(): LiveData<Uri> {
        return fileLiveData
    }

    fun updatePlaylists(playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.insertPlaylists(playlist)
        }
    }

    fun saveImage(context: Context, name: String, inputStream: InputStream?, time: Date) {
        val uri = interactor.saveImage(context, name, inputStream, time)
        fileLiveData.postValue(uri)
    }
}