package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.Interfaces.TrackInteractionListener
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.models.TracksState
import com.example.playlistmaker.search.presentation.SearchViewModel
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment(), TrackInteractionListener {

    private val viewModel: SearchViewModel by viewModel()
    // UI Elements
    private lateinit var searchField: EditText
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

    private val searchAdapter = TrackAdapter(mutableListOf(), this)
    private val historyAdapter = TrackAdapter(mutableListOf(), this)

    private lateinit var handler: Handler
    private val searchRunnable = Runnable { performSearch() }
    private val SEARCH_DEBOUNCE_DELAY = 2000L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)

        handler = Handler(Looper.getMainLooper())

        clearSearchButton.setOnClickListener { clearSearchField() }
        reloadButton.setOnClickListener {
            performSearch()
        }

        clearHistoryButton.setOnClickListener {
            viewModel.clearSearchHistory()
            historyLayout.visibility = View.GONE
        }

        searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch()
                true
            }
            false
        }

        searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(searchText: CharSequence?, start: Int, before: Int, count: Int) {
                clearSearchButton.visibility = if (searchText.isNullOrEmpty()) View.GONE else View.VISIBLE
                historyLayout.visibility = if (searchText.isNullOrEmpty()) View.VISIBLE else View.GONE
                searchDebounce()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        historyRecyclerView.adapter = historyAdapter

        searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        searchRecyclerView.adapter = searchAdapter

        observeViewModel()
        viewModel.loadSearchHistory()

        searchField.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchField.text.isEmpty() && viewModel.historyList.value?.isNotEmpty() == true) {
                showHistory()
            } else {
                historyLayout.visibility = View.GONE
            }
        }
    }

    private fun observeViewModel() {
        viewModel.tracksState.observe(viewLifecycleOwner, Observer { tracksState ->
            render(tracksState)
        })
        viewModel.historyList.observe(viewLifecycleOwner, Observer { historyTracks ->
            historyAdapter.updateTracks(historyTracks)
            if (searchField.hasFocus() && searchField.text.isEmpty() && historyTracks.isNotEmpty()) {
                showHistory()
            }
        })
    }

    private fun performSearch() {
        val searchText = searchField.text.toString().trim()
        if (searchText.isNotEmpty()) {
            errorLayout.visibility = View.GONE
            historyLayout.visibility = View.GONE
            viewModel.searchTracks(searchText)
        }
    }

    private fun setupUI(view: View) {
        progressBar = view.findViewById(R.id.progressBar)
        clearHistoryButton = view.findViewById(R.id.clear_history_button)
        historyLayout = view.findViewById(R.id.history_layout)
        errorLayout = view.findViewById(R.id.error_layout)
        errorImage = view.findViewById(R.id.error_img)
        errorText = view.findViewById(R.id.error_text)
        errorSubText = view.findViewById(R.id.error_sub_text)
        reloadButton = view.findViewById(R.id.reload_btn)
        clearSearchButton = view.findViewById(R.id.clearButton)
        searchField = view.findViewById(R.id.search_edit_text)
        searchRecyclerView = view.findViewById(R.id.search_results)
        historyRecyclerView = view.findViewById(R.id.history_recyclerview)
    }

    private fun clearSearchField() {
        historyLayout.visibility = View.GONE
        searchField.text.clear()
        val hideKeyboard = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        hideKeyboard.hideSoftInputFromWindow(searchField.windowToken, 0)
        searchRecyclerView.visibility = View.GONE
    }

    override fun onTrackSelected(track: Track) {
        viewModel.addTrackToHistory(track)
        val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
            putExtra("track", Gson().toJson(track))
        }
        startActivity(intent)
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun render(tracksState: TracksState) {
        when {
            tracksState.isLoading -> showLoading(true)
            else -> {
                showLoading(false)
                if (tracksState.isFailed != null) {
                    when {
                        tracksState.isFailed -> showConnectionError()
                        else -> showNoDataFound()
                    }
                } else {
                    if (tracksState.tracks.isEmpty()) {
                        showNoDataFound()
                    } else {
                        searchAdapter.tracks.clear()
                        searchAdapter.tracks.addAll(tracksState.tracks)
                        searchAdapter.notifyDataSetChanged()
                        showSearchSuccess()
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showHistory() {
        historyLayout.visibility = View.VISIBLE
        searchRecyclerView.visibility = View.GONE
        errorLayout.visibility = View.GONE
    }

    private fun showSearchSuccess() {
        errorLayout.visibility = View.GONE
        historyLayout.visibility = View.GONE
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

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }
}
