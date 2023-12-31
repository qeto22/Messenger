package com.kbachtbasi.messaging.utils

class ChatHelper {

    companion object {

        fun getChatId(currentUserId: String, friendUserId: String) : String {
            val userIdList = listOf(currentUserId, friendUserId).sorted()
            return userIdList[0] + ":" + userIdList[1]
        }

        fun getFriendId(chatId: String, currentUserId: String): String {
            return chatId.replace(currentUserId, "").replace(":", "")
        }

    }

}