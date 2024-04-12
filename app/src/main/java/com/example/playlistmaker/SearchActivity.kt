package com.example.playlistmaker

import TrackAdapter
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

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

    // Data and Adapter
    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(ITunesAPI::class.java)
    private var searchQuery: String = ""
    private var trackList = ArrayList<Track>()
    private var historyList = SearchHistory.load()
    private val SEARCH_QUERY_STATE_KEY = "search_query"
    private val historyAdapter = TrackAdapter(this, historyList)
    private val searchAdapter = TrackAdapter(this, trackList)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setupUI()

        backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        clearSearchButton.setOnClickListener { clearSearchField() }

        reloadButton.setOnClickListener { performSearch() }

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
        outState.putString(SEARCH_QUERY_STATE_KEY, searchQuery)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchQuery = savedInstanceState.getString(SEARCH_QUERY_STATE_KEY, "")
        searchField.setText(searchQuery)
    }
    private fun performSearch() {
        val searchText = searchField.text.toString().trim()
        if (searchText.isEmpty()) return

        iTunesService.search(searchText).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                val results = response.body()?.results.orEmpty()
                trackList.clear()
                trackList.addAll(results)
                searchAdapter.notifyDataSetChanged()

                if (trackList.isEmpty()) {
                    showNoDataFound()
                } else {
                    showSearchSuccess()
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                showConnectionError()
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
        historyList.clear()
        historyList.addAll(SearchHistory.load())
        historyAdapter.notifyDataSetChanged()
        historyLayout.visibility = View.VISIBLE
    }
}
