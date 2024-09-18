package com.example.playlistmaker.library.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.databinding.FragmentPlaylistsLibraryBinding
import com.example.playlistmaker.library.presentation.LibraryPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
class LibraryPlaylistsFragment : Fragment() {
    companion object {
        fun newInstance(): Fragment = LibraryPlaylistsFragment().apply {}
    }

    private val viewModel by viewModel<LibraryPlaylistViewModel>()
    private var _binding: FragmentPlaylistsLibraryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PlaylistAdapter { playlist ->
            val intent = Intent(requireContext(), PlaylistActivity::class.java)
            intent.putExtra("PLAYLIST_ID", playlist.id)
            startActivity(intent)
        }
        binding.playlistList.layoutManager = GridLayoutManager(requireActivity(), 2)
        binding.playlistList.adapter = adapter

        viewModel.returnPlaylists().observe(viewLifecycleOwner) { playlists ->
            adapter.submitList(playlists)
            if (playlists.isEmpty())
                showPlaceholder(true)
        }

        binding.newPlaylistButton.setOnClickListener {
            val intent = Intent(requireContext(), PlaylistCreatorActivity::class.java)
            startActivity(intent)
        }
        viewModel.getPlaylists()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showPlaceholder(boolean: Boolean) {
        binding.playlistsEmptyLayout.isVisible = boolean
        binding.playlistList.isVisible = !boolean
    }
}
