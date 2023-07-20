package com.kbachtbasi.messaging.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kbachtbasi.messaging.utils.Const
import com.kbachtbasi.messaging.utils.Message
import java.util.UUID

class ChatViewModel : ViewModel() {

    private val database: FirebaseDatabase = Firebase.database(Const.DB_URL)

    private val _unreadMessages = MutableLiveData<List<Message>>(emptyList())
    val unreadMessages: LiveData<List<Message>> get() = _unreadMessages

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun setMessageListener(chatId: String) {
        database.getReference("chats")
            .child(chatId)
            .orderByChild("sendTime")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val message: Message? = snapshot.getValue(Message::class.java)
                    message?.let {
                        val newMessagesList = mutableListOf<Message>()
                        newMessagesList.addAll(_unreadMessages.value!!)
                        newMessagesList.add(message)

                        _unreadMessages.value = newMessagesList
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    // TODO: თუ დავამატებთ შეტყობინების რედაქტირების ფუნქციონალს 
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    // TODO: თუ დავამატებთ შეტყობინების წაშლის ფუნქციონალს 
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    // TODO: ეს არასდროს არ მოხდება 
                }

                override fun onCancelled(error: DatabaseError) {
                    _error.value = error.message
                }
            })
    }

    fun sendMessage(chatId: String, message: Message) {
        database.getReference("chats")
            .child(chatId)
            .child(UUID.randomUUID().toString())
            .setValue(message)
    }

    fun clearUnreadMessages() {
        _unreadMessages.value = emptyList()
    }

}