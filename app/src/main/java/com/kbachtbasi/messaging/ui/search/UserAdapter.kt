package com.kbachtbasi.messaging.ui.search

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kbachtbasi.messaging.R
import com.kbachtbasi.messaging.databinding.UserItemBinding
import com.kbachtbasi.messaging.ui.chat.ChatActivity
import com.kbachtbasi.messaging.utils.Const
import com.kbachtbasi.messaging.utils.User

class UserAdapter(private var users: List<User>) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: UserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            if (user.profilePictureUrl.isBlank()) {
                Glide.with(binding.root)
                    .load(AppCompatResources.getDrawable(binding.root.context, R.drawable.avatar_image_placeholder))
                    .circleCrop()
                    .into(binding.profilePicture)
            } else {
                Glide.with(binding.root)
                    .load(user.profilePictureUrl)
                    .circleCrop()
                    .into(binding.profilePicture)
            }

            binding.nickname.text = user.nickname
            binding.profession.text = user.profession
            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, ChatActivity::class.java)
                intent.putExtra(Const.FRIEND_DATA, user)

                binding.root.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = users[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun setUserList(newUsers: List<User>?) {
        users = newUsers!!
        notifyDataSetChanged()
    }
}