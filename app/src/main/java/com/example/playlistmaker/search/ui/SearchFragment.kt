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
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.Interfaces.TrackInteractionListener
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.models.TracksState
import com.example.playlistmaker.search.presentation.SearchViewModel
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment(), TrackInteractionListener {

    private val viewModel: SearchViewModel by viewModel()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val searchAdapter = TrackAdapter(mutableListOf(), this)
    private val historyAdapter = TrackAdapter(mutableListOf(), this)
    private var isFirstRender = true
    private var clickJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.clearButton.setOnClickListener { clearSearchField() }
        binding.reloadBtn.setOnClickListener { performSearch() }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearSearchHistory()
            binding.historyLayout.visibility = View.GONE
        }

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch()
                true
            } else {
                false
            }
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(
                searchText: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                val isSearchTextEmpty = searchText.isNullOrEmpty()
                binding.clearButton.visibility = if (isSearchTextEmpty) View.GONE else View.VISIBLE
                showHistory(isSearchTextEmpty)
                viewModel.searchDebounce(searchText.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.historyRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.historyRecyclerview.adapter = historyAdapter

        binding.searchResults.layoutManager = LinearLayoutManager(requireContext())
        binding.searchResults.adapter = searchAdapter

        observeViewModel()
        viewModel.loadSearchHistory()

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            val shouldShowHistory = hasFocus && binding.searchEditText.text.isEmpty()
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
                if (binding.searchEditText.hasFocus() && binding.searchEditText.text.isEmpty()) {
                    showHistory(true)
                }
            }
        }
    }

    private fun performSearch() {
        val searchText = binding.searchEditText.text.toString().trim()
        if (searchText.isNotEmpty()) {
            showNothing()
            viewModel.searchTracks(searchText)
        }
    }

    private fun clearSearchField() {
        showNothing()
        binding.searchEditText.text.clear()
        val hideKeyboard =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        hideKeyboard.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    override fun onTrackSelected(track: Track) {
        clickDebounce {
            viewModel.addTrackToHistory(track)
            val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
                putExtra("track", Gson().toJson(track))
            }
            startActivity(intent)
        }
    }

    private fun clickDebounce(click: () -> Unit) {
        if (clickJob?.isActive == true) return
        clickJob = viewLifecycleOwner.lifecycleScope.launch {
            click()
            delay(CLICK_DELAY)
        }
    }

    private fun render(tracksState: TracksState) {
        if (isFirstRender) {
            isFirstRender = false
            return
        }
        when (tracksState) {
            is TracksState.Loading -> showLoading(true)
            is TracksState.Success -> {
                showLoading(false)
                if (tracksState.tracks.isEmpty()) {
                    showNoDataFound(true)
                } else {
                    searchAdapter.tracks.clear()
                    searchAdapter.tracks.addAll(tracksState.tracks)
                    searchAdapter.notifyDataSetChanged()
                    showSearchSuccess(true)
                }
            }

            is TracksState.Error -> {
                showLoading(false)
                if (tracksState.isNetworkError) {
                    showConnectionError(true)
                } else {
                    showNoDataFound(true)
                }
            }

            TracksState.Empty -> {
                showLoading(false)
                showNoDataFound(true)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showHistory(show: Boolean) {
        if (show && viewModel.historyList.value.isNotEmpty()) {
            binding.searchResults.visibility = View.GONE
            binding.errorLayout.visibility = View.GONE
            binding.historyLayout.visibility = View.VISIBLE
        } else {
            binding.historyLayout.visibility = View.GONE
        }
    }

    private fun showSearchSuccess(show: Boolean) {
        if (show) {
            binding.errorLayout.visibility = View.GONE
            binding.historyLayout.visibility = View.GONE
            binding.searchResults.visibility = View.VISIBLE
        } else {
            binding.searchResults.visibility = View.GONE
        }
    }

    private fun showNoDataFound(show: Boolean) {
        if (show) {
            binding.searchResults.visibility = View.GONE
            binding.errorSubText.visibility = View.GONE
            binding.reloadBtn.visibility = View.GONE
            binding.errorImg.setImageResource(R.drawable.no_data_found)
            binding.errorText.setText(R.string.no_data_found)
            binding.errorLayout.visibility = View.VISIBLE
        } else {
            binding.errorLayout.visibility = View.GONE
        }
    }

    private fun showConnectionError(show: Boolean) {
        if (show) {
            binding.searchResults.visibility = View.GONE
            binding.errorImg.setImageResource(R.drawable.connection_error)
            binding.errorText.setText(R.string.connection_error)
            binding.errorSubText.setText(R.string.check_connection)
            binding.errorLayout.visibility = View.VISIBLE
            binding.errorSubText.visibility = View.VISIBLE
            binding.reloadBtn.visibility = View.VISIBLE
        } else {
            binding.errorLayout.visibility = View.GONE
        }
    }

    private fun showNothing() {
        showLoading(false)
        showHistory(false)
        showSearchSuccess(false)
        showNoDataFound(false)
        showConnectionError(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CLICK_DELAY = 1000L
    }

}
