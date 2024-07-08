package com.example.playlistmaker.search.data

import com.example.playlistmaker.App
import com.example.playlistmaker.search.domain.Interfaces.TrackHistoryRepository
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class TrackHistoryRepositoryImpl : TrackHistoryRepository {
    companion object {
        private const val HISTORY_TRACK_LIST_KEY = "history_key"
        private const val MAX_HISTORY_SIZE = 10
        private val gson = Gson()
    }

    private fun save(historyTrackList: List<Track>) {
        val jsonTrackList = gson.toJson(historyTrackList)
        App.sharedPreferences.edit().putString(HISTORY_TRACK_LIST_KEY, jsonTrackList).apply()
    }

    override fun loadHistory(): List<Track> {
        return App.sharedPreferences.getString(HISTORY_TRACK_LIST_KEY, null)?.let { json ->
            val listType = object : TypeToken<List<Track>>() {}.type
            gson.fromJson(json, listType)
        } ?: emptyList()
    }

    override fun addTrack(track: Track) {
        val historyTrackList = loadHistory().toMutableList().apply {
            remove(track)
            if (size >= MAX_HISTORY_SIZE) {
                removeAt(size - 1)
            }
            add(0, track)
        }
        save(historyTrackList)
    }

    override fun clearHistory() {
        save(emptyList())
    }
}
