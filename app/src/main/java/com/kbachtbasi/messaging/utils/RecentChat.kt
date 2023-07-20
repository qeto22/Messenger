package com.kbachtbasi.messaging.utils

data class RecentChat(
    val chatId: String,
    val friend: User,
    val recentMessage: Message
)