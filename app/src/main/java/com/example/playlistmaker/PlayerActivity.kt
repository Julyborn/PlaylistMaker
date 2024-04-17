package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

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

    private val radius by lazy { 8 * resources.displayMetrics.density }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        initializeViews()
        val track = Gson().fromJson(intent.extras?.getString("track"), Track::class.java)
        updateUIWithTrackInfo(track)
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
