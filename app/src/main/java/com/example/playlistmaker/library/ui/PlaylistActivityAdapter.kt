package com.example.playlistmaker.library.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.playlistmaker.databinding.SearchItemBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackAdapter


class PlaylistActivityAdapter(
    private val onTrackClick: (Track) -> Unit,
    private val onTrackLongClick: (Track) -> Unit
) : ListAdapter<Track, TrackAdapter.TrackViewHolder>(
    object : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem.trackID == newItem.trackID
        }

        override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem == newItem
        }
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackAdapter.TrackViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        val binding = SearchItemBinding.inflate(layoutInspector, parent, false)
        return TrackAdapter.TrackViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: TrackAdapter.TrackViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onTrackClick(item)
        }
        holder.itemView.setOnLongClickListener {
            onTrackLongClick(item)
            true
        }
    }
}


