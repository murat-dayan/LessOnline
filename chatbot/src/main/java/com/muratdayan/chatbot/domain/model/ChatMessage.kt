package com.muratdayan.chatbot.domain.model

data class ChatMessage(
    val message: String,
    val isUser: Boolean // true: kullanıcı, false: bot
)
