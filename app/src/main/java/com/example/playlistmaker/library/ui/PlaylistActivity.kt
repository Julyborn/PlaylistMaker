package com.example.playlistmaker.library.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.library.domain.playlist.Playlist
import com.example.playlistmaker.library.presentation.PlaylistViewModel
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistActivity : AppCompatActivity() {
    private val viewModel by viewModel<PlaylistViewModel>()
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private var isClickAllowed = true
    private lateinit var tracks: List<Track>
    private lateinit var trackDelete: String

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            lifecycleScope.launch {
                delay(CLICK_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.loadPlaylist(intent)

        val tracksBottomSheet = BottomSheetBehavior.from(binding.playlistBottomSheet)
        tracksBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED

        val optionsBottomSheet = BottomSheetBehavior.from(binding.playlistOptionsBottomSheet)
        optionsBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN

        val emptyTracksMes = MaterialAlertDialogBuilder(this, R.style.toast)
            .setMessage(getString(R.string.empty_tracks_message))

        val trackDeleteDialog = MaterialAlertDialogBuilder(this, R.style.toast)
            .setTitle(getString(R.string.delete_track))
            .setMessage(getString(R.string.track_delete_message))
            .setNeutralButton(getString(R.string.cancel)) { _, _ -> }
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                viewModel.updatePlaylist(trackDelete)
                viewModel.bindAgain()
            }

        val deleteDialog = MaterialAlertDialogBuilder(this, R.style.toast)
            .setTitle(getString(R.string.delete_title))
            .setMessage(getString(R.string.delete_text))
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.deletePlaylist()
                finish()
            }

        val adapter = PlaylistActivityAdapter(
            onTrackClick = { track ->
                if (clickDebounce()) {
                    val displayIntent = Intent(this, PlayerActivity::class.java)
                    displayIntent.putExtra("track", Gson().toJson(track))
                    startActivity(displayIntent)
                }
            },
            onTrackLongClick = { track ->
                trackDelete = track.trackID.toString()
                trackDeleteDialog.show()
            }
        )

        adapter.submitList(listOf())
        binding.playlistSheetRv.adapter = adapter

        viewModel.returnPlaylist(intent).let { (playlistLiveData, tracksLiveData, tracksTimeLiveData) ->
            playlistLiveData.observe(this) { playlist ->
                bindStates(playlist)
            }

            tracksLiveData.observe(this) { tracks ->
                this.tracks = tracks
                adapter.submitList(tracks.reversed())
                binding.emptyPlaylistMessage.isVisible = tracks.isEmpty()
            }

            tracksTimeLiveData.observe(this) { trackTime ->
                binding.playlistTime.text = returnTime(trackTime)
            }
        }

        binding.playlistShareBut.setOnClickListener {
            if (tracks.isEmpty()) {
                emptyTracksMes.show()
            } else {
                sharePlaylist()
            }
        }

        binding.playlistOptionsBut.setOnClickListener {
            optionsBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.optionShare.setOnClickListener {
            if (tracks.isEmpty()) {
                emptyTracksMes.show()
            } else {
                sharePlaylist()
            }
        }

        binding.optionRename.setOnClickListener {
            viewModel.getPlaylistLiveData().value?.let { playlist ->
                val displayIntent = Intent(this, PlaylistCreatorActivity::class.java)
                displayIntent.putExtra("playlist", Gson().toJson(playlist))
                startActivity(displayIntent)
            }
        }

        binding.optionDelete.setOnClickListener {
            deleteDialog.show()
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadPlaylist(intent)
    }

    private fun sharePlaylist() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, viewModel.sharePlaylist())
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, "Share via"))
    }

    private fun bindStates(playlist: Playlist) {
        binding.playlistInfoView.text = playlist.info
        binding.playlistTrackCount.text = returnCount(playlist.count)
        binding.playlistNameView.text = playlist.name
        binding.optionsName.text = playlist.name
        binding.optionsCount.text = returnCount(playlist.count)
        Glide.with(binding.root)
            .load(playlist.image)
            .transform(CenterCrop(), RoundedCorners(2))
            .placeholder(R.drawable.ic_placeholder)
            .into(binding.optionsImage)
        Glide.with(binding.root).load(playlist.image)
            .transform(CenterCrop()).placeholder(
                R.drawable.ic_placeholder
            ).into(binding.playlistImage)
    }

    private fun returnCount(count: Int): String {
        return when {
            count in 11..14 -> "$count треков"
            count % 10 == 1 -> "$count трек"
            count % 10 in 2..4 -> "$count трека"
            else -> "$count треков"
        }
    }

    private fun returnTime(time: Long): String {
        val currentTime = SimpleDateFormat("mm", Locale.getDefault()).format(time).toInt()
        return if (currentTime in 11..14) {
            "$currentTime минут"
        } else if (currentTime % 10 == 1) {
            "$currentTime минута"
        } else if (currentTime % 10 in 2..4) {
            "$currentTime минуты"
        } else {
            "$currentTime минут"
        }
    }

    companion object {
        private const val CLICK_DELAY = 1000L
    }
}
