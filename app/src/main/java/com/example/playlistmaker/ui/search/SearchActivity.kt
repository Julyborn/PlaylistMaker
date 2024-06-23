package com.example.playlistmaker.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.TrackInteractionListener
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.player.PlayerActivity
import com.google.gson.Gson

class SearchActivity : AppCompatActivity(), TrackInteractionListener {

    // UI Elements
    private lateinit var searchField: EditText
    private lateinit var backButton: ImageView
    private lateinit var clearSearchButton: ImageView
    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var errorLayout: LinearLayout
    private lateinit var historyLayout: LinearLayout
    private lateinit var clearHistoryButton: Button
    private lateinit var errorImage: ImageView
    private lateinit var errorText: TextView
    private lateinit var errorSubText: TextView
    private lateinit var reloadButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var trackInteractor: TrackInteractor


    private var searchQuery: String = ""
    private var trackList = ArrayList<Track>()
    private var historyList = SearchHistory.load()
    private val searchAdapter = TrackAdapter(this, trackList, this)
    private val historyAdapter = TrackAdapter(this, historyList, this)
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { performSearch() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setupUI()
        trackInteractor = Creator.provideTrackInteractor()
        backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        clearSearchButton.setOnClickListener { clearSearchField() }
        reloadButton.setOnClickListener {
            errorLayout.visibility = View.GONE
            performSearch()
        }

        clearHistoryButton.setOnClickListener {
            historyList.clear()
            SearchHistory.clear()
            historyLayout.visibility = View.GONE
        }

        searchField.setText(searchQuery)
        searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch()
                true
            }
            false
        }

        searchField.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && searchField.text.isEmpty() && historyList.isNotEmpty()) {
                historyLayout.visibility = View.VISIBLE
                errorLayout.visibility = View.GONE
                searchRecyclerView.visibility = View.GONE
            } else {
                historyLayout.visibility = View.GONE
            }
        }

        searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(searchText: CharSequence?, start: Int, before: Int, count: Int) {
                clearSearchButton.visibility = if (searchText.isNullOrEmpty()) View.GONE else View.VISIBLE
                if (searchField.hasFocus() && searchText.isNullOrEmpty() && historyList.isNotEmpty()) {
                    historyLayout.visibility = View.VISIBLE
                    errorLayout.visibility = View.GONE
                    searchRecyclerView.visibility = View.GONE
                } else {
                    historyLayout.visibility = View.GONE
                }
                searchDebounce()
            }
            override fun afterTextChanged(s: Editable?) {
                searchQuery = s.toString()
            }
        })

        val historyLayoutManager = LinearLayoutManager(this)
        historyRecyclerView = findViewById(R.id.history_recyclerview)
        historyRecyclerView.layoutManager = historyLayoutManager
        historyRecyclerView.adapter = historyAdapter

        val searchLayoutManager = LinearLayoutManager(this)
        searchRecyclerView = findViewById(R.id.search_results)
        searchRecyclerView.layoutManager = searchLayoutManager
        searchRecyclerView.adapter = searchAdapter
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(Companion.SEARCH_QUERY_STATE_KEY, searchQuery)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchQuery = savedInstanceState.getString(Companion.SEARCH_QUERY_STATE_KEY, "")
        searchField.setText(searchQuery)
    }
    private fun performSearch() {
        val searchText = searchField.text.toString().trim()
        if (searchText.isEmpty()) return
        progressBar.visibility = View.VISIBLE

        trackInteractor.searchTracks(searchText, object : TrackInteractor.TrackConsumer {
            override fun consume(foundTracks: List<Track>) {
                val updateTrackListRunnable = Runnable {
                    progressBar.visibility = View.GONE
                    trackList.clear()
                    if (foundTracks.isEmpty()) {
                        showNoDataFound()
                    } else {
                        trackList.addAll(foundTracks)
                        searchAdapter.notifyDataSetChanged()
                        showSearchSuccess()
                    }
                }
                handler.post(updateTrackListRunnable)
            }

            override fun onError(e: Exception) {
                val errorRunnable = Runnable {
                    progressBar.visibility = View.GONE
                    showConnectionError()
                }
                handler.post(errorRunnable)
            }
        })
    }
    private fun showSearchSuccess() {
        errorLayout.visibility = View.GONE
        searchRecyclerView.visibility = View.VISIBLE
    }
    private fun showNoDataFound() {
        searchRecyclerView.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
        errorImage.setImageResource(R.drawable.no_data_found)
        errorSubText.visibility = View.GONE
        errorText.setText(R.string.no_data_found)
        reloadButton.visibility = View.GONE
    }
    private fun showConnectionError() {
        searchRecyclerView.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
        errorImage.setImageResource(R.drawable.connection_error)
        errorSubText.visibility = View.VISIBLE
        errorText.setText(R.string.connection_error)
        errorSubText.setText(R.string.check_connection)
        reloadButton.visibility = View.VISIBLE
    }
    private fun setupUI() {
        progressBar = findViewById(R.id.progressBar)
        clearHistoryButton = findViewById(R.id.clear_history_button)
        historyLayout = findViewById(R.id.history_layout)
        errorLayout = findViewById(R.id.error_layout)
        errorImage = findViewById(R.id.error_img)
        errorText = findViewById(R.id.error_text)
        errorSubText = findViewById(R.id.error_sub_text)
        reloadButton = findViewById(R.id.reload_btn)
        clearSearchButton = findViewById(R.id.clearButton)
        backButton = findViewById(R.id.back_button)
        searchField = findViewById(R.id.search_edit_text)
        searchField.setText(searchQuery)
    }
    private fun clearSearchField() {
        searchField.text.clear()
        val hideKeyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        hideKeyboard.hideSoftInputFromWindow(searchField.windowToken, 0)
        trackList.clear()
        searchRecyclerView.visibility = View.GONE
        clearSearchButton.visibility = View.GONE
    }
    override fun onTrackSelected(track: Track) {
        if (clickDebounce()) {
            historyList.clear()
            historyList.addAll(SearchHistory.load())
            historyAdapter.notifyDataSetChanged()
            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra("track", Gson().toJson(track))
            }
            startActivity(intent)
        }
    }
    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        const private val SEARCH_QUERY_STATE_KEY = "search_query"
    }
}
