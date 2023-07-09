package com.kbachtbasi.messaging

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignUpViewModel : ViewModel() {

    val db = Firebase.database(Const.DB_URL);
    val auth = FirebaseAuth.getInstance()

    val signUpSuccessLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val signUpErrorLiveData: MutableLiveData<String> = MutableLiveData()

    fun signUpUser(username: String, password: String, profession: String) {
        if (username.isEmpty() || password.isEmpty()) {
            signUpErrorLiveData.value = "Please enter valid email and password"
            return
        }

        val email = "$username@messaging.com"
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveUserData(username, profession)
                    signUpSuccessLiveData.value = true
                } else {
                    when (task.exception) {
                        is FirebaseAuthInvalidUserException, is FirebaseAuthInvalidCredentialsException -> {
                            signUpErrorLiveData.value = "Invalid username or password"
                        }
                        is FirebaseAuthUserCollisionException -> {
                            signUpErrorLiveData.value = "User with given username[$username] already exists"
                        }
                        else -> {
                            signUpErrorLiveData.value = "Unexpected error occurred."
                        }
                    }
                }
            }
    }

    private fun saveUserData(username: String, profession: String) {
        db.getReference("users")
            .child(username)
            .child("profession")
            .setValue(profession)
    }
}