package com.muratdayan.chat.data.model

import com.google.firebase.Timestamp

data class ChatModel(
    val chatId: String ="",
    val participiants: List<String> = listOf(),
    val createdAt: Timestamp? = null,
)
