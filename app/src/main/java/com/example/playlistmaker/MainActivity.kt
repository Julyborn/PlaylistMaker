package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSearch: Button = findViewById(R.id.search_button)
        val buttonLibrary: Button = findViewById(R.id.library_button)
        val buttonSettings: Button = findViewById(R.id.settings_button)

        buttonSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        buttonLibrary.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                startActivity(Intent(this@MainActivity, LibraryActivity::class.java))
            }
        })

        buttonSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}