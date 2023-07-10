package com.kbachtbasi.messaging.ui.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kbachtbasi.messaging.utils.Const
import java.util.UUID

class SignUpViewModel : ViewModel() {

    private val db = Firebase.database(Const.DB_URL);
    private val auth = FirebaseAuth.getInstance()

    val signUpSuccessLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val signUpErrorLiveData: MutableLiveData<String> = MutableLiveData()

    fun signUpUser(nickname: String, password: String, profession: String) {
        if (nickname.isEmpty() || password.isEmpty()) {
            signUpErrorLiveData.value = "Please enter valid email and password"
            return
        }

        db.getReference("user-email-mapping")
            .child(nickname)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userAlreadyExists = snapshot.value != null
                    var email = "${UUID.randomUUID()}@gmail.com"
                    if (userAlreadyExists) {
                        email = snapshot.getValue(String::class.java)!!
                    }

                    signUpUserInternal(email, nickname, password, profession)
                }

                override fun onCancelled(error: DatabaseError) {
                    signUpErrorLiveData.value = error.message
                }
            })


    }

    private fun signUpUserInternal(generatedEmail: String,
                                   nickname: String,
                                   password: String,
                                   profession: String) {

        auth.createUserWithEmailAndPassword(generatedEmail, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveUserData(generatedEmail, nickname, profession)
                    signUpSuccessLiveData.value = true
                } else {
                    when (task.exception) {
                        is FirebaseAuthInvalidUserException, is FirebaseAuthInvalidCredentialsException -> {
                            signUpErrorLiveData.value = "Invalid username or password"
                        }
                        is FirebaseAuthUserCollisionException -> {
                            signUpErrorLiveData.value = "User with given username[$nickname] already exists"
                        }
                        else -> {
                            signUpErrorLiveData.value = "Unexpected error occurred."
                        }
                    }
                }
            }
    }

    private fun saveUserData(generatedEmail: String, nickname: String, profession: String) {
        db.getReference("user-email-mapping")
            .child(nickname)
            .setValue(generatedEmail)
        db.getReference("users")
            .child(auth.currentUser!!.uid)
            .child("profession")
            .setValue(profession)
        db.getReference("users")
            .child(auth.currentUser!!.uid)
            .child("nickname")
            .setValue(nickname)
    }
}