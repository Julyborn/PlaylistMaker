package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentFavoritesLibraryBinding
import com.example.playlistmaker.library.presentation.LibraryFavoritesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryFavoritesFragment : Fragment() {

    private val viewModel: LibraryFavoritesViewModel by viewModel()
    private var _binding: FragmentFavoritesLibraryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = LibraryFavoritesFragment()
    }
}
