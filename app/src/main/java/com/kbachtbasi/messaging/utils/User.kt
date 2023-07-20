package com.kbachtbasi.messaging.utils

import java.io.Serializable

data class User(
    val nickname: String = "",
    var id: String = "",
    val profession: String = "",
    val profilePictureUrl: String = ""
) : Serializable