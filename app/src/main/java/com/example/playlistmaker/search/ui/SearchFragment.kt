package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.Interfaces.TrackInteractionListener
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.models.TracksState
import com.example.playlistmaker.search.presentation.SearchViewModel
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
    private var isFirstRender = true
    private var isClickAllowed = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)

        clearSearchButton.setOnClickListener { clearSearchField() }
        reloadButton.setOnClickListener { performSearch() }

        clearHistoryButton.setOnClickListener {
            viewModel.clearSearchHistory()
            historyLayout.visibility = View.GONE
        }

        searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch()
                true
            } else {
                false
            }
        }

        searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(searchText: CharSequence?, start: Int, before: Int, count: Int) {
                val isSearchTextEmpty = searchText.isNullOrEmpty()
                clearSearchButton.visibility = if (isSearchTextEmpty) View.GONE else View.VISIBLE
                showHistory(isSearchTextEmpty)
                viewModel.searchDebounce(searchText.toString())
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
            val shouldShowHistory = hasFocus && searchField.text.isEmpty()
            showHistory(shouldShowHistory)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.tracksState.collectLatest { tracksState ->
                render(tracksState)
            }
        }

        lifecycleScope.launch {
            viewModel.historyList.collectLatest { historyTracks ->
                historyAdapter.updateTracks(historyTracks)
                if (searchField.hasFocus() && searchField.text.isEmpty()) {
                    showHistory(true)
                }
            }
        }
    }

    private fun performSearch() {
        val searchText = searchField.text.toString().trim()
        if (searchText.isNotEmpty()) {
            showNothing()
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
    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(Companion.CLICK_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }
    private fun clearSearchField() {
        showNothing()
        searchField.text.clear()
        val hideKeyboard = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        hideKeyboard.hideSoftInputFromWindow(searchField.windowToken, 0)

    }

    override fun onTrackSelected(track: Track) {
        viewModel.addTrackToHistory(track)
        val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
            putExtra("track", Gson().toJson(track))
        }
        startActivity(intent)
    }

    private fun render(tracksState: TracksState) {
        if (isFirstRender) {
            isFirstRender = false
            return
        }
        when {
            tracksState.isLoading -> showLoading(true)
            else -> {
                showLoading(false)
                if (tracksState.isFailed != null) {
                    when {
                        tracksState.isFailed -> showConnectionError(true)
                        else -> showNoDataFound(true)
                    }
                } else {
                    if (tracksState.tracks.isEmpty()) {
                        showNoDataFound(true)
                    } else {
                        searchAdapter.tracks.clear()
                        searchAdapter.tracks.addAll(tracksState.tracks)
                        searchAdapter.notifyDataSetChanged()
                        showSearchSuccess(true)
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showHistory(show: Boolean) {
        if (show && viewModel.historyList.value.isNotEmpty()) {
            searchRecyclerView.visibility = View.GONE
            errorLayout.visibility = View.GONE
            historyLayout.visibility = View.VISIBLE
        }
        else
            historyLayout.visibility = View.GONE
    }

    private fun showSearchSuccess(show: Boolean) {
        if (show) {
            errorLayout.visibility = View.GONE
            historyLayout.visibility = View.GONE
            searchRecyclerView.visibility = View.VISIBLE
        }
        else {
            searchRecyclerView.visibility = View.GONE
        }
    }

    private fun showNoDataFound(show: Boolean) {
        if (show) {
            searchRecyclerView.visibility = View.GONE
            errorSubText.visibility = View.GONE
            reloadButton.visibility = View.GONE
            errorImage.setImageResource(R.drawable.no_data_found)
            errorText.setText(R.string.no_data_found)
            errorLayout.visibility = View.VISIBLE

        }
        else{
            errorLayout.visibility = View.GONE
        }
    }

    private fun showConnectionError(show: Boolean) {
        if (show) {
            searchRecyclerView.visibility = View.GONE
            errorImage.setImageResource(R.drawable.connection_error)
            errorText.setText(R.string.connection_error)
            errorSubText.setText(R.string.check_connection)
            errorLayout.visibility = View.VISIBLE
            errorSubText.visibility = View.VISIBLE
            reloadButton.visibility = View.VISIBLE
        }
        else {
            errorLayout.visibility = View.GONE
        }

    }
    private fun showNothing() {
        showLoading(false)
        showHistory(false)
        showSearchSuccess(false)
        showNoDataFound(false)
        showConnectionError(false)
    }

    companion object {
        private const val CLICK_DELAY = 1000L
    }


}
