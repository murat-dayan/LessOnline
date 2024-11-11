package com.muratdayan.chatbot.data.remote.repository


import com.muratdayan.chatbot.data.remote.model.ChatRequestModel
import com.muratdayan.chatbot.data.remote.model.ChatResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ChatApiService {

    @Headers("Content-Type: application/json")
    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    suspend fun getChatbotResponse(
        @Header("Authorization") idToken: String,
        @Query("key") apiKey: String,
        @Body request: ChatRequestModel
    ) : Response<ChatResponseModel>
}