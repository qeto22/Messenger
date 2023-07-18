package com.kbachtbasi.messaging.ui.chat

import android.util.LayoutDirection
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.kbachtbasi.messaging.R
import com.kbachtbasi.messaging.databinding.ChatItemBinding
import com.kbachtbasi.messaging.utils.Message
import java.text.DateFormat
import java.util.Locale

class ChatAdapter(private var messages: List<Message>, private val currentUserId: String) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ChatItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            if (message.senderId != currentUserId) {
                binding.chatItemLayout.layoutDirection = View.LAYOUT_DIRECTION_RTL
                binding.textMessage.background = AppCompatResources.getDrawable(binding.root.context, R.drawable.chat_background_gray)
                binding.textMessage.setTextColor(binding.root.context.getColor(R.color.friendsMessageColor))
            }

            binding.textMessage.text = message.message
            binding.messageTime.text = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault()).format(message.sendTime)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = messages[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

}