package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val UPDATE_TIME_INFO = 500L
    }
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
    private lateinit var playTime: TextView

    private val radius by lazy { 8 * resources.displayMetrics.density }
    private var playerState = STATE_DEFAULT
    private val mediaPlayer = MediaPlayer()
    private var mainThreadHandler: Handler? = null
    private var url: String? = null

    private val cycleRunnable = object : Runnable {
        override fun run() {
            val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
            Log.d("TIME", formattedTime)
            playTime.text = formattedTime
            mainThreadHandler?.postDelayed(this, UPDATE_TIME_INFO)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        mainThreadHandler = Handler(Looper.getMainLooper())
        initializeViews()
        val track = Gson().fromJson(intent.extras?.getString("track"), Track::class.java)
        updateUIWithTrackInfo(track)
        preparePlayer()
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
        playTime = findViewById(R.id.playtime)
        playButton = findViewById(R.id.play_but)
        playButton.setOnClickListener { playbackControl() }
        backButton = findViewById(R.id.back)
        backButton.setOnClickListener { finish() }
    }

    private fun updateUIWithTrackInfo(track: Track) {
        titleName.text = track.trackName
        artistName.text = track.artistName
        loadImage(track.artworkUrl100)
        trackLength.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        updateAlbumVisibility(track.collectionName)
        trackYear.text = extractYear(track.releaseDate)
        trackStyle.text = track.primaryGenreName
        trackCountry.text = track.country
        url = track.previewUrl
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

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        mainThreadHandler?.removeCallbacks(cycleRunnable)
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun preparePlayer() {
        mediaPlayer.apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener {
                playerState = STATE_PREPARED
            }
            setOnCompletionListener {
                playerState = STATE_PREPARED
                mainThreadHandler?.removeCallbacks(cycleRunnable)
                playTime.text = getString(R.string.time_placeholder)
                playButton.setBackgroundResource(R.drawable.ic_play)
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setBackgroundResource(R.drawable.ic_pause)
        playerState = STATE_PLAYING
        mainThreadHandler?.postDelayed(cycleRunnable, UPDATE_TIME_INFO)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setBackgroundResource(R.drawable.ic_play)
        playerState = STATE_PAUSED
        mainThreadHandler?.removeCallbacks(cycleRunnable)
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PAUSED, STATE_PREPARED -> startPlayer()
        }
    }
}
