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

    private lateinit var searchEditText: EditText
    private lateinit var backButton: ImageView
    private lateinit var clearButton: ImageView
    private lateinit var recyclerView: RecyclerView

    private lateinit var errorLayout: LinearLayout

    private lateinit var errorImage: ImageView
    private lateinit var errorText: TextView
    private lateinit var errorSubText: TextView
    private lateinit var reloadButton: Button


    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(ITunesAPI::class.java)

    private var searchQuery: String = ""
    private var trackList = ArrayList<Track>()
    private val SEARCH_QUERY_STATE_KEY = "search_query"
    private val adapter = TrackAdapter(this, trackList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        errorLayout = findViewById(R.id.error_layout)
        errorImage = findViewById(R.id.error_img)
        errorText = findViewById(R.id.error_text)
        errorSubText = findViewById(R.id.error_sub_text)
        reloadButton = findViewById(R.id.reload_btn)

        backButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        clearButton = findViewById(R.id.clearButton)
        clearButton.setOnClickListener {
            searchEditText.text.clear()
            val hideKeyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            hideKeyboard.hideSoftInputFromWindow(searchEditText.windowToken, 0)
            trackList.clear()
            recyclerView.visibility = View.GONE
            clearButton.visibility = View.GONE
        }

        reloadButton.setOnClickListener { performSearch() }

        searchEditText = findViewById(R.id.search_edit_text)
        searchEditText.setText(searchQuery)
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    performSearch()
                    true
                }
                false
            }
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }

            override fun afterTextChanged(s: Editable?) {
                searchQuery = s.toString()
            }
        })
        recyclerView = findViewById(R.id.search_results)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        recyclerView.adapter = adapter
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY_STATE_KEY, searchQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchQuery = savedInstanceState.getString(SEARCH_QUERY_STATE_KEY, "")
        searchEditText.setText(searchQuery)
    }
    private fun performSearch() {
        val searchText = searchEditText.text.toString().trim()
        if (searchText.isEmpty()) return

        iTunesService.search(searchText).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                val results = response.body()?.results.orEmpty()
                trackList.clear()
                trackList.addAll(results)
                adapter.notifyDataSetChanged()

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
        recyclerView.visibility = View.VISIBLE
    }

    private fun showNoDataFound() {
        recyclerView.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
        errorImage.setImageResource(R.drawable.no_data_found)
        errorSubText.visibility = View.GONE
        errorText.setText(R.string.no_data_found)
        reloadButton.visibility = View.GONE
    }

    private fun showConnectionError() {
        recyclerView.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
        errorImage.setImageResource(R.drawable.connection_error)
        errorSubText.visibility = View.VISIBLE
        errorText.setText(R.string.connection_error)
        errorSubText.setText(R.string.check_connection)
        reloadButton.visibility = View.VISIBLE
    }

}
