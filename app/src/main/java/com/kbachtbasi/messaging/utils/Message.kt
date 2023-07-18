package com.kbachtbasi.messaging.utils

import java.util.Date

data class Message(
    var sendTime: Date = Date(),
    var message: String = "",
    var senderId: String = "",
)