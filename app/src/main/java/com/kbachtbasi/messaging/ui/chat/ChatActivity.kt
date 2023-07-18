package com.kbachtbasi.messaging.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.kbachtbasi.messaging.R
import com.kbachtbasi.messaging.databinding.ActivityChatBinding
import com.kbachtbasi.messaging.utils.Message

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

        val messages = listOf(Message(message = "Hello World", senderId = "Test"), Message(message = "Hello Keto!"))

        binding.messagesRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
        binding.messagesRecyclerView.adapter = ChatAdapter(messages, "Test")
    }
}