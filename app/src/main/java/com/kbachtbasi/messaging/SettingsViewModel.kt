package com.kbachtbasi.messaging

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.values
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.UUID

class SettingsViewModel : ViewModel() {

    private val _profilePictureUrl = MutableLiveData<String>()
    val profilePictureUrl: LiveData<String>
        get() = _profilePictureUrl

    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String>
        get() = _nickname

    private val _editedNickname = MutableLiveData<String>()
    val editedNickname: LiveData<String>
        get() = _editedNickname

    private val _profession = MutableLiveData<String>()
    val profession: LiveData<String>
        get() = _profession

    private val _editedProfession = MutableLiveData<String>()
    val editedProfession: LiveData<String>
        get() = _editedProfession

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _editInProgress = MutableLiveData(false)
    val editInProgress: LiveData<Boolean>
        get() = _editInProgress

    private val mAuth: FirebaseAuth = Firebase.auth
    private val mDatabase: FirebaseDatabase = Firebase.database(Const.DB_URL)
    private val mStorage: FirebaseStorage = FirebaseStorage.getInstance()

    fun fetchData() {
        mDatabase.getReference("users")
            .child(mAuth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _nickname.value = snapshot.child("name").getValue(String::class.java)
                    _profession.value = snapshot.child("profession").getValue(String::class.java)
                    _profilePictureUrl.value = snapshot.child("profilePictureUrl").getValue(String::class.java)
                }

                override fun onCancelled(error: DatabaseError) {
                    _error.value = error.message
                }
            })
    }

    fun logout() {
        mAuth.signOut()
    }

    fun nicknameEdited(nickname: String) {
        _editedNickname.value = nickname
    }

    fun professionEdited(profession: String) {
        _editedProfession.value = profession
    }

    fun startEdit() {
        _editInProgress.value = true
        _editedNickname.value = nickname.value
        _editedProfession.value = profession.value
    }

    fun finishEdit() {
        _editInProgress.value = false

        if (editedNickname.value == nickname.value && editedProfession.value == profession.value) {
            _error.value = null
            return
        }

        if (editedNickname.value == nickname.value) {
            updateUserProfessionInDb()
            return
        }

        mDatabase.getReference("user-email-mapping")
            .child(editedNickname.value!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userAlreadyExists = snapshot.value != null
                    if (userAlreadyExists) {
                        _error.value = "User with given nickname already exists!"
                        _editedNickname.value = nickname.value
                        return
                    }

                    changeDetailsInDb()
                }

                override fun onCancelled(error: DatabaseError) {
                    _error.value = error.message
                }
            })
    }

    private fun changeDetailsInDb() {
        mDatabase.getReference("user-email-mapping")
            .child(nickname.value!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    updateUserEmailMappingInDb(snapshot.getValue(String::class.java)!!)
                }

                override fun onCancelled(error: DatabaseError) {
                    _error.value = error.message
                }
            })
    }

    private fun updateUserEmailMappingInDb(value: String) {
        mDatabase.getReference("user-email-mapping")
            .child(editedNickname.value!!)
            .setValue(value)
            .addOnCompleteListener {
                removeOldMappingFromDb()
            }
            .addOnFailureListener {
                _error.value = it.message
            }
    }

    private fun removeOldMappingFromDb() {
        mDatabase.getReference("user-email-mapping")
            .child(nickname.value!!)
            .removeValue()
            .addOnCompleteListener {
                updateUserNicknameInDb()
            }
            .addOnFailureListener {
                _error.value = it.message
            }
    }

    private fun updateUserNicknameInDb() {
        mDatabase.getReference("users")
            .child(mAuth.currentUser!!.uid)
            .child("name")
            .setValue(editedNickname.value)
            .addOnCompleteListener {
                if (profession.value != editedProfession.value) {
                    updateUserProfessionInDb()
                } else {
                    _error.value = null
                }
            }
            .addOnFailureListener {
                _error.value = it.message
            }
    }

    private fun updateUserProfessionInDb() {
        mDatabase.getReference("users")
            .child(mAuth.currentUser!!.uid)
            .child("profession")
            .setValue(editedProfession.value)
            .addOnCompleteListener {
                _nickname.value = _editedNickname.value
                _profession.value = _editedProfession.value
                _error.value = null
            }
            .addOnFailureListener {
                _error.value = it.message
            }
    }

    fun uploadImage(it: Uri) {
        val imagePath = "images/${UUID.randomUUID()}"
        val imageRef = mStorage.reference.child(imagePath)

        imageRef.putFile(it)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnCompleteListener {
                    mDatabase.getReference("users")
                        .child(mAuth.currentUser!!.uid)
                        .child("profilePictureUrl")
                        .setValue(it.result.toString())
                        .addOnCompleteListener {
                            _error.value = null
                        }
                        .addOnFailureListener {
                            _error.value = it.message
                        }
                }.addOnFailureListener {
                    _error.value = it.message
                }
        }.addOnFailureListener { e ->
            _error.value = e.message
        }
    }
}