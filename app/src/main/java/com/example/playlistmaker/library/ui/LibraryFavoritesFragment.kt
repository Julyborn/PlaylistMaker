package com.example.playlistmaker.library.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.FragmentFavoritesLibraryBinding
import com.example.playlistmaker.library.data.db.TrackEntity
import com.example.playlistmaker.library.presentation.LibraryFavoritesViewModel
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.Interfaces.TrackInteractionListener
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryFavoritesFragment : Fragment(), TrackInteractionListener {

    private val viewModel: LibraryFavoritesViewModel by viewModel()
    private var _binding: FragmentFavoritesLibraryBinding? = null
    private val binding get() = _binding!!

    private var clickJob: Job? = null
    private val favoriteAdapter = TrackAdapter(mutableListOf(), this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.favRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.favRecyclerview.adapter = favoriteAdapter
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.tracksLiveData.observe(viewLifecycleOwner, Observer { tracks ->
            render(tracks)
            Log.d("Бд", "Количество треков: ${tracks.size}")


        })
    }

    private fun render(tracks: List<TrackEntity>) {
        if (tracks.isEmpty()) {
            showNoFavoritesFound(true)
        } else {
            favoriteAdapter.tracks.clear()
            favoriteAdapter.tracks.addAll(tracks.map { entity -> entity.toTrack() })
            favoriteAdapter.notifyDataSetChanged()
            showNoFavoritesFound(false)
        }
    }

    private fun showNoFavoritesFound(show: Boolean) {
        binding.favEmptyLayout.visibility = if (show) View.VISIBLE else View.GONE
        binding.favLayout.visibility = if (show) View.GONE else View.VISIBLE
    }

    override fun onTrackSelected(track: Track) {
        clickDebounce {
            val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
                putExtra("track", Gson().toJson(track))
            }
            startActivity(intent)
        }
    }

    private fun clickDebounce(click: () -> Unit) {
        if (clickJob?.isActive == true) return
        clickJob = viewLifecycleOwner.lifecycleScope.launch {
            click()
            delay(CLICK_DELAY)
        }
    }

    fun TrackEntity.toTrack(): Track {
        return Track(
            trackID = this.trackId,
            trackName = this.trackName,
            artistName = this.artistName,
            trackTime = this.trackTime,
            artworkUrl = this.artworkUrl,
            collectionName = this.collectionName,
            releaseDate = this.releaseDate,
            primaryGenreName = this.primaryGenreName,
            country = this.country,
            previewUrl = this.trackUrl,
            isFavorite = true
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CLICK_DELAY = 1000L
        fun newInstance() = LibraryFavoritesFragment()
    }
}
