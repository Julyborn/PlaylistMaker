package com.example.playlistmaker.library.presentation

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.playlist.Playlist
import com.example.playlistmaker.library.domain.playlist.PlaylistInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistViewModel(
    private val interactor: PlaylistInteractor
) : ViewModel() {

    private val playlistLiveData = MutableLiveData<Playlist>()
    private val tracksLiveData = MutableLiveData<List<Track>>()
    private val tracksTimeLiveData = MutableLiveData<Long>()
    private val shareIntentLiveData = MutableLiveData<Intent>()

    private lateinit var thisPlaylist: Playlist
    private var thisTracks: List<Track> = listOf()
    private var trackTime = 0L


    fun returnPlaylist(intent: Intent): Triple<LiveData<Playlist>, LiveData<List<Track>>, LiveData<Long>> {
        getPlaylist(intent)
        return Triple(playlistLiveData, tracksLiveData, tracksTimeLiveData)
    }

    fun getPlaylistLiveData(): LiveData<Playlist> = playlistLiveData
    fun getTracksLiveData(): LiveData<List<Track>> = tracksLiveData
    fun getTracksTimeLiveData(): LiveData<Long> = tracksTimeLiveData
    fun getShareIntentLiveData(): LiveData<Intent> = shareIntentLiveData

    fun sharePlaylist() : String{
        val logTag = "запускаем PlaylistShare"
        var text = "${thisPlaylist.name}\n" +
                "${thisPlaylist.info}\n" +
                "${getTrackCount(thisPlaylist.count)}:\n"

        var count = 1
        thisTracks.forEach { track ->
            text += "$count. ${track.artistName} - ${track.trackName} (${track.trackTime})\n"
            count++
        }
        Log.d(logTag, "Сообщение для отправки:\n$text")
        return (text)
    }

    fun deletePlaylist() {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.deletePlaylist(thisPlaylist)
        }
    }
    fun deleteTrack(trackid:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.deleteTrack(thisPlaylist,trackid)
        }
    }
    fun updatePlaylist(id: String) {
        viewModelScope.launch {
            interactor.updatePlaylist(
                Playlist(
                    name = thisPlaylist.name,
                    image = thisPlaylist.image,
                    count = thisPlaylist.count - 1,
                    info = thisPlaylist.info,
                    content = thisPlaylist.content.replace("$id, ", ""),
                    id = thisPlaylist.id
                )
            )
        }
    }

    fun bindAgain() {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.getPlaylist(thisPlaylist.id).collect { playlist ->
                thisPlaylist = playlist
                getPlaylistTracks(playlist.content)
                bind()
            }
        }
    }

    private fun getPlaylist(intent: Intent) {
        val playlistJson = intent.extras?.getString("playlist")

        if (playlistJson.isNullOrEmpty()) {
            val playlistId = intent.extras?.getLong("PLAYLIST_ID") ?: return
            viewModelScope.launch(Dispatchers.IO) {
                interactor.getPlaylist(playlistId).collect { playlist ->
                    thisPlaylist = playlist
                    getPlaylistTracks(playlist.content)
                    bind()
                }
            }
        } else {
            try {
                thisPlaylist = Gson().fromJson(playlistJson, Playlist::class.java)
            } catch (e: Exception) {
                thisPlaylist = Playlist("", "", 0, "", "", 0L)
            }
            getPlaylistTracks(thisPlaylist.content)
        }
    }


    private fun getPlaylistTracks(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.getPlaylistTracks().collect { tracks ->
                thisTracks = tracks.filter { track -> track.trackID.toString() in id }
                trackTime = thisTracks.sumOf { track -> parseTrackTimeToMillis(track.trackTime) }
                bind()
            }
        }
    }

    private fun parseTrackTimeToMillis(trackTime: String): Long {
        val parts = trackTime.split(":")
        return if (parts.size == 2) {
            val minutes = parts[0].toLongOrNull() ?: 0L
            val seconds = parts[1].toLongOrNull() ?: 0L
            (minutes * 60 + seconds) * 1000
        } else {
            0L
        }
    }

    private fun bind() {
        playlistLiveData.postValue(thisPlaylist)
        tracksLiveData.postValue(thisTracks)
        tracksTimeLiveData.postValue(trackTime)
    }

    fun loadPlaylist(intent: Intent) {
        val playlistId = intent.getLongExtra("PLAYLIST_ID", -1)
        if (playlistId != -1L) {
            viewModelScope.launch(Dispatchers.IO) {
                interactor.getPlaylist(playlistId).collect { playlist ->
                    thisPlaylist = playlist
                    getPlaylistTracks(playlist.content)
                    bind()
                }
            }
        }
    }

    private fun getTrackCount(count: Int): String {
        return when {
            count in 11..14 -> "$count треков"
            count % 10 == 1 -> "$count трек"
            count % 10 in 2..4 -> "$count трека"
            else -> "$count треков"
        }
    }
}
