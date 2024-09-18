package com.example.playlistmaker.library.ui

import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistCreatorBinding
import com.example.playlistmaker.library.domain.playlist.Playlist
import com.example.playlistmaker.library.presentation.PlaylistCreatorViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistCreatorFragment : Fragment() {

    private var _binding: FragmentPlaylistCreatorBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<PlaylistCreatorViewModel>()
    private lateinit var newName: String
    private var imageUri: String = ""
    private var isChanged = false
    private var originalPlaylist: Playlist? = null
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    private val nameWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            toggleCreateButtonState()
        }
        override fun afterTextChanged(p0: Editable?) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                updatePlaylistImage(it)
                imageUri = it.toString()
                isChanged = true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistCreatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeViewModel()
        handleBackPressed()

        val playlistJson = arguments?.getString("playlist")
        if (playlistJson != null) {
            originalPlaylist = Gson().fromJson(playlistJson, Playlist::class.java)
            fillFieldsFromPlaylist(originalPlaylist!!)
            binding.createPlaylistBut.text = getString(R.string.save)
        } else {
            binding.createPlaylistBut.isEnabled = false
        }
    }

    private fun setupListeners() {
        binding.playlistName.addTextChangedListener(nameWatcher)
        binding.playlistName.setOnFocusChangeListener { _, hasFocus ->
            toggleEditTextFocus(hasFocus, binding.playlistName, binding.newNameTitle)
        }
        binding.newPlaylistInfEt.setOnFocusChangeListener { _, hasFocus ->
            toggleEditTextFocus(hasFocus, binding.newPlaylistInfEt, binding.newInfoTitle)
        }
        binding.backButton.setOnClickListener {
            handleQuitAction()
        }
        binding.newPlaylistImage.setOnClickListener {
            launchImagePicker()
        }
        binding.createPlaylistBut.setOnClickListener {
            createPlaylist()
        }
    }

    private fun observeViewModel() {
        viewModel.getFile().observe(viewLifecycleOwner) { uri ->
            savePlaylists(uri.toString())
        }
    }

    private fun handleBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleQuitAction()
            }
        })
    }

    private fun handleQuitAction() {
        if (isChanged || binding.newPlaylistInfEt.text.isNotEmpty() || binding.playlistName.text.isNotEmpty()) {
            showQuitDialog()
        } else {
            requireActivity().finish()
        }
    }

    private fun showQuitDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.dialog)
            .setTitle(getString(R.string.playlist_creator_toast_title))
            .setMessage(getString(R.string.playlist_creator_toast_message))
            .setNeutralButton(getString(R.string.playlist_creator_resume), null)
            .setPositiveButton(getString(R.string.playlist_creator_cancel)) { _, _ ->
                requireActivity().finish()
            }
            .show()
    }

    private fun launchImagePicker() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun updatePlaylistImage(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .centerCrop()
            .transform(CenterCrop(), RoundedCorners(16))
            .into(binding.newPlaylistImage)
    }

    private fun toggleCreateButtonState() {
        val isEmpty = binding.playlistName.text.isEmpty()
        binding.createPlaylistBut.isEnabled = !isEmpty
    }

    private fun toggleEditTextFocus(hasFocus: Boolean, editText: EditText, label: TextView) {
        val shouldShowLabel = hasFocus || editText.text.isNotEmpty()
        label.isVisible = shouldShowLabel
        val backgroundRes = if (shouldShowLabel) R.drawable.playlist_edittext_tint_active else R.drawable.playlist_edittext_tint
        editText.setBackgroundResource(backgroundRes)
    }

    private fun createPlaylist() {
        newName = binding.playlistName.text.toString()
        val currentTime = Calendar.getInstance().time

        if (isChanged && imageUri.isNotEmpty()) {
            viewModel.saveImage(
                newName,
                requireContext().contentResolver.openInputStream(imageUri.toUri()),
                currentTime
            )
        } else {
            savePlaylists(imageUri)
        }
    }

    private fun savePlaylists(savedImageUri: String) {
        if (originalPlaylist == null) {
            viewModel.updatePlaylists(
                Playlist(
                    newName,
                    savedImageUri,
                    0,
                    binding.newPlaylistInfEt.text.toString(),
                    ""
                )
            )
            Toast.makeText(requireContext(), "Плейлист $newName создан", Toast.LENGTH_LONG).show()
        } else {
            viewModel.updatePlaylists(
                originalPlaylist!!.copy(
                    name = newName,
                    image = savedImageUri,
                    info = binding.newPlaylistInfEt.text.toString()
                )
            )
        }
        requireActivity().finish()
    }

    private fun fillFieldsFromPlaylist(playlist: Playlist) {
        binding.playlistName.setText(playlist.name)
        binding.newPlaylistInfEt.setText(playlist.info)

        if (!playlist.image.isNullOrEmpty()) {
            val imageUri = Uri.parse(playlist.image)
            updatePlaylistImage(imageUri)
            this.imageUri = imageUri.toString()
            isChanged = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
