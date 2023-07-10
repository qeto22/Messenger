package com.kbachtbasi.messaging.ui.signup

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.kbachtbasi.messaging.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private lateinit var viewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)

        binding.signUpBtn.setOnClickListener {
            if (isFormValid()) {
                saveCustomer()
            }
        }

        viewModel.signUpSuccessLiveData.observe(this, Observer { success ->
            binding.progressBarWrapper.visibility = View.GONE
            if (success) {
                finish()
            }
        })

        viewModel.signUpErrorLiveData.observe(this, Observer { errorMessage ->
            binding.progressBarWrapper.visibility = View.GONE
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
        })
    }

    private fun saveCustomer() {
        val username = binding.usernameInput.text.toString()
        val password = binding.passwordInput.text.toString()
        val profession = binding.professionInput.text.toString()

        binding.progressBarWrapper.visibility = View.VISIBLE
        viewModel.signUpUser(username, password, profession)
    }

    private fun isFormValid(): Boolean {
        var isFormValid = true

        val username = binding.usernameInput.text.toString()
        val password = binding.passwordInput.text.toString()
        val profession = binding.professionInput.text.toString()

        if (username.trim() == "") {
            binding.usernameInput.error = "Please enter username"
            isFormValid = false
        }

        if (password.trim() == "") {
            binding.passwordInput.error = "Please enter password"
            isFormValid = false
        }

        if (profession.trim() == "") {
            binding.professionInput.error = "Please enter your profession"
            isFormValid = false
        }

        return isFormValid
    }
}