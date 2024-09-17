package com.example.playlistmaker.library.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistItemBinding
import com.example.playlistmaker.library.domain.playlist.Playlist

class PlaylistAdapter(
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    private var playlists: List<Playlist> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = PlaylistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener {
         //   interactionListener.onPlaylistSelected(playlist)
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    fun submitList(newPlaylists: List<Playlist>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

    class PlaylistViewHolder(private val binding: PlaylistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val radius: Float = 8 * itemView.resources.displayMetrics.density

        fun bind(model: Playlist) = with(binding) {
            playlistName.text = model.name
            playlistCount.text = bindTracks(model.count)
            Glide.with(itemView.context).load(model.image)
                .transform(CenterCrop(), RoundedCorners(radius.toInt()))
                .placeholder(R.drawable.ic_placeholder)
                .into(playlistImg)
        }

        private fun bindTracks(count: Int): String {
            return when {
                count in 11..14 -> "$count треков"
                count % 10 == 1 -> "$count трек"
                count % 10 in 2..4 -> "$count трека"
                else -> "$count треков"
            }
        }
    }
}
