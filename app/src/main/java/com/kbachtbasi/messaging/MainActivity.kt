package com.kbachtbasi.messaging

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kbachtbasi.messaging.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mAuth = Firebase.auth
        mAuth.addAuthStateListener {
            val user = it.currentUser
            if (user == null) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(binding.mainActivityFragmentContainer.id, HomeFragment())
            .commit()

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(binding.mainActivityFragmentContainer.id, HomeFragment())
                        .commit()
                    return@setOnItemSelectedListener true
                }

                R.id.settings -> {
                    supportFragmentManager.beginTransaction()
                        .replace(binding.mainActivityFragmentContainer.id, SettingsFragment())
                        .commit()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }
}