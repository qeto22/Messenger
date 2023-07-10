package com.kbachtbasi.messaging.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kbachtbasi.messaging.utils.Const
import com.kbachtbasi.messaging.databinding.ActivityLoginBinding
import com.kbachtbasi.messaging.ui.signup.SignUpActivity
import com.kbachtbasi.messaging.ui.main.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        database = Firebase.database(Const.DB_URL)

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

                database.getReference("user-email-mapping")
                    .child(username)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val userExists = snapshot.value != null
                            if (userExists) {
                                val email = snapshot.getValue(String::class.java)!!
                                loginInternal(email, password)
                                return
                            }

                            binding.progressBarWrapper.visibility = View.GONE
                            Snackbar.make(
                                binding.root,
                                "User with given nick name does not exists!",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            binding.progressBarWrapper.visibility = View.GONE
                            Snackbar.make(binding.root, error.message, Snackbar.LENGTH_SHORT).show()
                        }
                    })


            }
        }

        binding.signUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    fun loginInternal(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                binding.progressBarWrapper.visibility = View.GONE
                if (!it.isSuccessful) {
                    Snackbar.make(
                        binding.root,
                        it.exception!!.message.toString(),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
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