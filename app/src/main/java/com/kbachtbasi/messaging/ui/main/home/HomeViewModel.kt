package com.kbachtbasi.messaging.ui.main.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kbachtbasi.messaging.utils.ChatHelper
import com.kbachtbasi.messaging.utils.Const
import com.kbachtbasi.messaging.utils.Message
import com.kbachtbasi.messaging.utils.RecentChat
import com.kbachtbasi.messaging.utils.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel() {

    private val userReference = Firebase.database(Const.DB_URL)
        .getReference("users")
    private val chatsRef = Firebase.database(Const.DB_URL)
        .getReference("chats")

    private val currentUserId: String = Firebase.auth.currentUser!!.uid

    private val userInfoCache: MutableMap<String, User> = mutableMapOf()

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> get() = _searchQuery

    private val _recentUserChats = MutableLiveData<List<RecentChat>>()
    val recentUserChats: LiveData<List<RecentChat>> get() = _recentUserChats

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun onQueryChanged(query: String) {
        _searchQuery.value = query
        fetchRecentChatWithUpdatedQuery()
    }

    fun fetchRecentChatWithUpdatedQuery() {
        chatsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                onMessageFetched(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                _error.value = error.message
            }

        })
    }

    private fun onMessageFetched(snapshot: DataSnapshot) {
        viewModelScope.launch {
            val currentUserChats: List<RecentChat> = snapshot.children.filter {
                it.key!!.contains(currentUserId)
            }.map { chat ->
                chat.children.map { message ->
                    message.getValue(Message::class.java)!!
                }.map {
                    val userInfo = getUserInfo(chat.key!!)
                    RecentChat(chat.key!!, userInfo, it)
                }.sortedByDescending { recentChat ->
                    recentChat.recentMessage.sendTime
                }.first()
            }.filter {
                it.friend.nickname.startsWith(searchQuery.value!!, false)
            }

            _recentUserChats.value = currentUserChats
        }
    }

    private suspend fun getUserInfo(chatId: String): User {
        val friendId = ChatHelper.getFriendId(chatId, currentUserId)
        if (userInfoCache.containsKey(friendId)) {
            return userInfoCache[friendId]!!
        }

        return withContext(Dispatchers.IO) {
            try {
                val dataSnapshot = userReference.child(friendId).get().await()
                val user = dataSnapshot.getValue(User::class.java)
                if (user != null) {
                    user.id = dataSnapshot.key!!
                    userInfoCache[friendId] = user
                }
                user
            } catch (e: Exception) {
                Log.e("ERROR", e.message.toString())
                null
            }
        } ?: User()
    }

}