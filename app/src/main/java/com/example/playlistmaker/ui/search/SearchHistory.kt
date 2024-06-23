package com.example.playlistmaker.ui.search

import com.example.playlistmaker.App
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory {
    companion object {
        private const val HISTORY_TRACK_LIST_KEY = "history_key"
        private const val MAX_HISTORY_SIZE = 10
        private val gson = Gson()

        private fun save(historyTrackList: ArrayList<Track>) {
            val jsonTrackList = gson.toJson(historyTrackList)
            App.sharedPreferences.edit().putString(HISTORY_TRACK_LIST_KEY, jsonTrackList).apply()
        }

        fun load(): ArrayList<Track> {
            return App.sharedPreferences.getString(HISTORY_TRACK_LIST_KEY, null)?.let { json ->
                val listType = object : TypeToken<ArrayList<Track>>() {}.type
                gson.fromJson(json, listType)
            } ?: ArrayList()
        }

        fun add(track: Track) {
            val historyTrackList = load().apply {
                remove(track)
                if (size >= MAX_HISTORY_SIZE) {
                    removeAt(size - 1)
                }
                add(0,track)
            }
            save(historyTrackList)
        }

        fun clear() {
            save(ArrayList())
        }
    }
}
