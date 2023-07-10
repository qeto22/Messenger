package com.kbachtbasi.messaging.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kbachtbasi.messaging.utils.Const
import com.kbachtbasi.messaging.utils.User

class UserViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val database: FirebaseDatabase = Firebase.database(Const.DB_URL)
    private val usersRef: DatabaseReference = database.reference.child("users")

    private val _usersListLiveData: MutableLiveData<List<User>> = MutableLiveData()
    val usersListLiveData: LiveData<List<User>> = _usersListLiveData

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val batchSize = 20
    private var lastUserId: String? = null

    fun loadUsers() {
        usersRef.orderByKey().limitToFirst(batchSize).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val users: MutableList<User> = mutableListOf()
                for (userSnapshot in dataSnapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    user?.let {
                        user.id = userSnapshot.key.toString()
                        if (user.id != auth.currentUser!!.uid) {
                            users.add(it)
                        }
                    }
                }
                lastUserId = users.lastOrNull()?.id
                _usersListLiveData.postValue(users)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                _error.value = databaseError.message
            }
        })
    }

    fun searchUsers(query: String) {
        val queryRef = usersRef.orderByChild("nickname").startAt(query).endAt(query + "\uf8ff").limitToFirst(batchSize)
        queryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val users: MutableList<User> = mutableListOf()
                for (userSnapshot in dataSnapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    user?.let {
                        user.id = userSnapshot.key.toString()
                        if (user.id != auth.currentUser!!.uid) {
                            users.add(it)
                        }
                    }
                }
                _usersListLiveData.postValue(users)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                _error.value = databaseError.message
            }
        })
    }

    fun loadMoreUsers() {
        lastUserId?.let { lastId ->
            usersRef.orderByKey().startAfter(lastId).limitToFirst(batchSize).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val users: MutableList<User> = mutableListOf()
                    for (userSnapshot in dataSnapshot.children) {
                        val user = userSnapshot.getValue(User::class.java)
                        user?.let { users.add(it) }
                    }
                    lastUserId = users.lastOrNull()?.id
                    val currentUsers = _usersListLiveData.value.orEmpty().toMutableList()
                    currentUsers.addAll(users)
                    _usersListLiveData.postValue(currentUsers)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    _error.value = databaseError.message
                }
            })
        }
    }
}