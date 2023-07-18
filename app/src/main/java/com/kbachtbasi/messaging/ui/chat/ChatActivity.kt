package com.kbachtbasi.messaging.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kbachtbasi.messaging.R
import com.kbachtbasi.messaging.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.messageTextWrapper.setOnClickListener {
            binding.messageText.requestFocus()
        }

        binding.sendMessageBtn.setOnClickListener {
            binding.messageText.setText("")
        }
    }
}