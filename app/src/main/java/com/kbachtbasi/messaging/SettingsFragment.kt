package com.kbachtbasi.messaging

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kbachtbasi.messaging.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        viewModel.editInProgress.observe(viewLifecycleOwner) {
            if (it) {
                binding.nickname.visibility = View.GONE
                binding.nicknameEditText.visibility = View.VISIBLE
                binding.nicknameEditText.requestFocus()
                binding.profession.visibility = View.GONE
                binding.professionEditText.visibility = View.VISIBLE


                binding.updateBtn.text = "Save"
            } else {
                binding.nickname.visibility = View.VISIBLE
                binding.nicknameEditText.visibility = View.GONE
                binding.profession.visibility = View.VISIBLE
                binding.professionEditText.visibility = View.GONE

                binding.updateBtn.text = "Update"
            }
        }

        viewModel.profilePictureUrl.observe(viewLifecycleOwner) { imageUrl ->
            if (imageUrl != null) {
                Glide.with(this)
                    .load(imageUrl)
                    .circleCrop()
                    .into(binding.profilePicture)
            }
        }

        viewModel.nickname.observe(viewLifecycleOwner) { nickname ->
            binding.nickname.text = nickname
        }

        viewModel.editedNickname.observe(viewLifecycleOwner) { editedNickname ->
            if (binding.nicknameEditText.text.toString() != editedNickname) {
                binding.nicknameEditText.setText(editedNickname)
            }
        }

        viewModel.profession.observe(viewLifecycleOwner) { profession ->
            binding.profession.text = profession
        }

        viewModel.editedProfession.observe(viewLifecycleOwner) { editedProfession ->
            if (binding.professionEditText.text.toString() != editedProfession) {
                binding.professionEditText.setText(editedProfession)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            binding.progressBarWrapper.visibility = View.GONE
            if (error != null) {
                Snackbar.make(binding.root, error, Snackbar.LENGTH_SHORT).show()
            }
        }

        viewModel.fetchData()

        binding.logout.setOnClickListener {
            viewModel.logout()
        }

        binding.updateBtn.setOnClickListener {
            if (viewModel.editInProgress.value == true) {
                if (isFormValid()) {
                    binding.progressBarWrapper.visibility = View.VISIBLE
                    viewModel.finishEdit()
                }
            } else {
                viewModel.startEdit()
            }
        }

        binding.nicknameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                viewModel.nicknameEdited(s.toString())
            }

        })

        binding.professionEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                viewModel.professionEdited(s.toString())
            }

        })

        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.progressBarWrapper.visibility = View.VISIBLE
                viewModel.uploadImage(it)
            }
        }

        binding.profilePicture.setOnClickListener {
            getContent.launch("image/*")
        }

        return binding.root
    }

    private fun isFormValid(): Boolean {
        var isFormValid = true

        val nickname = binding.nicknameEditText.text.toString()
        val profession = binding.professionEditText.text.toString()

        if (nickname.trim() == "") {
            binding.nicknameEditText.error = "Please enter username"
            isFormValid = false
        }

        if (profession.trim() == "") {
            binding.professionEditText.error = "Please enter profession"
            isFormValid = false
        }

        return isFormValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}