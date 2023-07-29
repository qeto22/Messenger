package com.kbachtbasi.messaging.ui.chat

import android.os.Bundle
import android.util.TypedValue
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kbachtbasi.messaging.databinding.ActivityChatBinding
import com.kbachtbasi.messaging.utils.ChatHelper
import com.kbachtbasi.messaging.utils.Const
import com.kbachtbasi.messaging.utils.Message
import com.kbachtbasi.messaging.utils.User
import kotlin.math.abs


class ChatActivity : AppCompatActivity() {

    private lateinit var viewModel: ChatViewModel
    private lateinit var binding: ActivityChatBinding

    private lateinit var friend: User

    private var currentUserId: String = Firebase.auth.currentUser!!.uid
    private lateinit var friendUserId: String
    private lateinit var chatId: String

    private var chatAdapter = ChatAdapter(mutableListOf(), currentUserId)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        friend = intent.getSerializableExtra(Const.FRIEND_DATA) as User
        friendUserId = friend.id
        chatId = ChatHelper.getChatId(currentUserId, friendUserId)

        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        viewModel.unreadMessages.observe(this) { unreadMessages ->
            if (unreadMessages.isNotEmpty()) {
                chatAdapter.addItems(unreadMessages)
                viewModel.clearUnreadMessages()
            }
        }
        viewModel.error.observe(this) { errorMessage ->
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
        }
        viewModel.setMessageListener(chatId)


        binding = ActivityChatBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.collapsingToolbar.title = friend.nickname
        binding.collapsingToolbar.subtitle = friend.profession
        if (friend.profilePictureUrl.isNotBlank()) {
            Glide.with(binding.root)
                .load(friend.profilePictureUrl)
                .circleCrop()
                .into(binding.avatar)
        }

        binding.messageTextWrapper.setOnClickListener {
            binding.messageText.requestFocus()
        }

        binding.sendMessageBtn.setOnClickListener {
            val message = Message(message = binding.messageText.text.toString(), senderId = currentUserId)
            viewModel.sendMessage(chatId, message)
            binding.messageText.setText("")
        }

        binding.messagesRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
        binding.messagesRecyclerView.adapter = chatAdapter

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.collapsingAppbar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val collapsedPercentage =
                abs(verticalOffset) / appBarLayout.totalScrollRange.toFloat()

            val imageViewLayoutParams = binding.avatar.layoutParams as ViewGroup.MarginLayoutParams
            imageViewLayoutParams.bottomMargin = dpToPx((24 * (1 - collapsedPercentage)))
            binding.avatar.layoutParams = imageViewLayoutParams
        }
    }

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, resources
                .displayMetrics
        ).toInt()
    }
}