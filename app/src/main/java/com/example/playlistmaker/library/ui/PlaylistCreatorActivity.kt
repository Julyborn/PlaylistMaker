package com.example.playlistmaker.library.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R

class PlaylistCreatorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_creator)
        val playlistJson = intent.getStringExtra("playlist")
        val playlistCreatorFragment = PlaylistCreatorFragment()
        val bundle = Bundle()
        bundle.putString("playlist", playlistJson)
        playlistCreatorFragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, playlistCreatorFragment)
            .commit()
    }
}
