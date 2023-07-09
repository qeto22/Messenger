package com.kbachtbasi.messaging

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kbachtbasi.messaging.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

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