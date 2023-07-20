package com.kbachtbasi.messaging.ui.main.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kbachtbasi.messaging.R
import com.kbachtbasi.messaging.databinding.RecentChatItemBinding
import com.kbachtbasi.messaging.ui.chat.ChatActivity
import com.kbachtbasi.messaging.utils.Const
import com.kbachtbasi.messaging.utils.RecentChat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecentChatAdapter(private var users: List<RecentChat>) : RecyclerView.Adapter<RecentChatAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: RecentChatItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recentChat: RecentChat) {
            if (recentChat.friend.profilePictureUrl.isBlank()) {
                Glide.with(binding.root)
                    .load(AppCompatResources.getDrawable(binding.root.context, R.drawable.avatar_image_placeholder))
                    .circleCrop()
                    .into(binding.profilePicture)
            } else {
                Glide.with(binding.root)
                    .load(recentChat.friend.profilePictureUrl)
                    .circleCrop()
                    .into(binding.profilePicture)
            }

            binding.nickname.text = recentChat.friend.nickname
            binding.lastMessage.text = recentChat.recentMessage.message
            binding.timePassed.text = getTimePassedLabel(recentChat.recentMessage.sendTime)
            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, ChatActivity::class.java)
                intent.putExtra(Const.FRIEND_DATA, recentChat.friend)

                binding.root.context.startActivity(intent)
            }
        }
    }

    private fun getTimePassedLabel(sendTime: Long): CharSequence {
        val currentTime = System.currentTimeMillis()
        val timePassed = currentTime - sendTime

        val timePassedInSeconds = timePassed / 1_000
        val timePassedInMinutes = timePassedInSeconds / 60
        val timePassedInHours = timePassedInMinutes / 60

        return if (timePassedInHours < 1) {
            timePassedInMinutes.toInt().toString() + " min"
        } else if (timePassedInHours < 24) {
            timePassedInHours.toInt().toString() + " hour"
        } else {
            convertTimestampToDate(sendTime)
        }
    }

    private fun convertTimestampToDate(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("d MMM", Locale.ENGLISH)
        return format.format(date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecentChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = users[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return users.size
    }
}