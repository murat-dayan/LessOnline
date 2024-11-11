package com.muratdayan.chatbot.data.remote.model

data class ContentModel(
    val role: String,
    val parts: List<PartModel>,
)
