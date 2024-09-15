package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.presentation.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private val radius by lazy { 8 * resources.displayMetrics.density }
    private lateinit var currentTrack: Track
    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(Gson().fromJson(intent.extras?.getString("track"), Track::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentTrack = Gson().fromJson(intent.extras?.getString("track"), Track::class.java)
        updateUIWithTrackInfo(currentTrack)
        viewModel.preparePlayer(currentTrack.previewUrl)
        viewModel.isTrackFavoriteLiveData(currentTrack.trackID).observe(this, Observer { isFavorite ->
              binding.favoriteBut.setBackgroundResource(
                if (isFavorite) R.drawable.ic_favorite_active else R.drawable.ic_favorite
            )
        })


        binding.playBut.setOnClickListener {
            if (viewModel.isPlaying.value == true) {
                viewModel.pausePlayer()
            } else {
                viewModel.startPlayer()
            }
        }

        binding.favoriteBut.setOnClickListener { viewModel.favButClicked(currentTrack) }

        binding.back.setOnClickListener { finish() }

        viewModel.playTime.observe(this, Observer { binding.playerTimer.text = it })
        viewModel.isPlaying.observe(this, Observer { isPlaying ->
            binding.playBut.setBackgroundResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
        })
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releasePlayer()
    }

    private fun updateUIWithTrackInfo(track: Track) {
        binding.titleName.text = track.trackName
        binding.artistName.text = track.artistName
        loadImage(track.artworkUrl)
        binding.lengthDetail.text = track.trackTime
        updateAlbumVisibility(track.collectionName)
        binding.yearDetail.text = extractYear(track.releaseDate)
        binding.styleDetail.text = track.primaryGenreName
        binding.countryDetail.text = track.country
    }

    private fun loadImage(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl.replaceAfterLast('/', "512x512bb.jpg"))
            .centerCrop()
            .transform(RoundedCorners(radius.toInt()))
            .placeholder(R.drawable.ic_search_placeholder)
            .into(binding.img)
    }

    private fun updateAlbumVisibility(albumName: String) {
        if (albumName.isEmpty()) {
            binding.albumDetail.visibility = View.GONE
            binding.album.visibility = View.GONE
        } else {
            binding.albumDetail.visibility = View.VISIBLE
            binding.album.visibility = View.VISIBLE
            binding.albumDetail.text = albumName
        }
    }

    private fun extractYear(dateString: String): String = dateString.replaceAfter('-', "").substring(0, 4)
}
