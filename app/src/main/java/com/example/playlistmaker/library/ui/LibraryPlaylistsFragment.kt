package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentPlaylistsLibraryBinding
import com.example.playlistmaker.library.presentation.LibraryPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryPlaylistsFragment : Fragment() {

    private val viewModel: LibraryPlaylistViewModel by viewModel()
    private var _binding: FragmentPlaylistsLibraryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = LibraryPlaylistsFragment()
    }
}
