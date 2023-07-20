package com.kbachtbasi.messaging.utils

import java.io.Serializable

data class User(
    var nickname: String = "",
    var id: String = "",
    var profession: String = "",
    var profilePictureUrl: String = ""
) : Serializable