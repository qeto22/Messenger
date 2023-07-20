package com.kbachtbasi.messaging.utils

data class Message(
    var sendTime: Long = System.currentTimeMillis(),
    var message: String = "",
    var senderId: String = "",
)