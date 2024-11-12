package com.muratdayan.chat.data.model

import com.google.firebase.Timestamp

data class MessageModel(
    val messageId: String = "",
    val receiverId: String = "",
    val senderId: String = "",
    val content: String = "",
    val timestamp: Timestamp? = null,
)
