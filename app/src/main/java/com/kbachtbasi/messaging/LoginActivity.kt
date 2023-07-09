package com.kbachtbasi.messaging

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.kbachtbasi.messaging.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()
        auth.addAuthStateListener {
            val user = auth.currentUser?.uid
            user?.let {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.signInBtn.setOnClickListener {
            if (isFormValid()) {
                binding.progressBarWrapper.visibility = View.VISIBLE

                val username = binding.usernameInput.text.toString()
                val password = binding.passwordInput.text.toString()

                val email = "$username@messaging.com"
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        binding.progressBarWrapper.visibility = View.GONE
                        if (!it.isSuccessful) {
                            Snackbar.make(binding.root, it.exception!!.message.toString(), Snackbar.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        binding.signUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isFormValid(): Boolean {
        var isFormValid = true

        val username = binding.usernameInput.text.toString()
        val password = binding.passwordInput.text.toString()

        if (username.trim() == "") {
            binding.usernameInput.error = "Please enter username"
            isFormValid = false
        }

        if (password.trim() == "") {
            binding.passwordInput.error = "Please enter password"
            isFormValid = false
        }

        return isFormValid
    }
}