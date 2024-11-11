package com.muratdayan.chatbot.domain.repository

import com.google.firebase.auth.FirebaseUser
import com.muratdayan.chatbot.data.remote.model.ChatResponseModel
import retrofit2.Response

interface IChatRepository {
    suspend fun getChatResponse(idToken:String, message: String): Response<ChatResponseModel>
    fun getCurrentUser(): FirebaseUser?
    fun getUserData(userId: String, callback: (Map<String, Any>?) -> Unit)
}