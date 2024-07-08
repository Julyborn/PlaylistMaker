package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.presentation.PlayerViewModel
import com.example.playlistmaker.player.presentation.PlayerViewModelFactory
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson




class PlayerActivity : AppCompatActivity() {

    private lateinit var titleName: TextView
    private lateinit var artistName: TextView
    private lateinit var playerImg: ImageView
    private lateinit var trackLength: TextView
    private lateinit var trackAlbum: TextView
    private lateinit var trackYear: TextView
    private lateinit var trackStyle: TextView
    private lateinit var trackCountry: TextView
    private lateinit var staticTrackAlbum: TextView
    private lateinit var backButton: ImageView
    private lateinit var playButton: ImageButton
    private lateinit var playerTimer: TextView

    private lateinit var viewModel: PlayerViewModel

    private val radius by lazy { 8 * resources.displayMetrics.density }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        initializeViews()

        val interactor = Creator.providePlayerInteractor()
        val factory = PlayerViewModelFactory(interactor)

        viewModel = ViewModelProvider(this, factory).get(PlayerViewModel::class.java)

        val track = Gson().fromJson(intent.extras?.getString("track"), Track::class.java)
        updateUIWithTrackInfo(track)
        viewModel.preparePlayer(track.previewUrl)

        playButton.setOnClickListener {
            if (viewModel.isPlaying.value == true) {
                viewModel.pausePlayer()
            } else {
                viewModel.startPlayer()
            }
        }

        backButton.setOnClickListener { finish() }

        viewModel.playTime.observe(this, Observer { playerTimer.text = it })
        viewModel.isPlaying.observe(this, Observer { isPlaying ->
            playButton.setBackgroundResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
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

    private fun initializeViews() {
        titleName = findViewById(R.id.title_name)
        artistName = findViewById(R.id.artist_name)
        playerImg = findViewById(R.id.img)
        trackLength = findViewById(R.id.length_detail)
        trackAlbum = findViewById(R.id.album_detail)
        trackYear = findViewById(R.id.year_detail)
        trackStyle = findViewById(R.id.style_detail)
        trackCountry = findViewById(R.id.country_detail)
        staticTrackAlbum = findViewById(R.id.album)
        playerTimer = findViewById(R.id.player_timer)
        playButton = findViewById(R.id.play_but)
        backButton = findViewById(R.id.back)
    }

    private fun updateUIWithTrackInfo(track: Track) {
        titleName.text = track.trackName
        artistName.text = track.artistName
        loadImage(track.artworkUrl)
        trackLength.text = track.trackTime
        updateAlbumVisibility(track.collectionName)
        trackYear.text = extractYear(track.releaseDate)
        trackStyle.text = track.primaryGenreName
        trackCountry.text = track.country
    }

    private fun loadImage(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl.replaceAfterLast('/', "512x512bb.jpg"))
            .centerCrop()
            .transform(RoundedCorners(radius.toInt()))
            .placeholder(R.drawable.ic_search_placeholder)
            .into(playerImg)
    }

    private fun updateAlbumVisibility(albumName: String) {
        if (albumName.isEmpty()) {
            trackAlbum.visibility = View.GONE
            staticTrackAlbum.visibility = View.GONE
        } else {
            trackAlbum.visibility = View.VISIBLE
            staticTrackAlbum.visibility = View.VISIBLE
            trackAlbum.text = albumName
        }
    }

    private fun extractYear(dateString: String): String = dateString.replaceAfter('-', "").substring(0, 4)
}
