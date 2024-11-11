package com.muratdayan.chatbot.data.remote.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.core.UserData
import com.muratdayan.chatbot.data.remote.model.ChatRequestModel
import com.muratdayan.chatbot.data.remote.model.ChatResponseModel
import com.muratdayan.chatbot.data.remote.model.ContentModel
import com.muratdayan.chatbot.data.remote.model.PartModel
import com.muratdayan.chatbot.domain.repository.IChatRepository
import retrofit2.Response
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    private val chatApiService: ChatApiService,
    private val apiKey:String
) : IChatRepository{

    override suspend fun getChatResponse(idToken:String,message:String): Response<ChatResponseModel>{

        val chatRequestModel = ChatRequestModel(
            contents = listOf( // 'contents' alanını kullanıyoruz
                ContentModel(
                    role = "user",
                    parts = listOf(
                        PartModel(
                            text = message // Kullanıcının gönderdiği mesaj
                        )
                    )
                )
            )
        )

        return chatApiService.getChatbotResponse(idToken = idToken, apiKey = "AIzaSyAvAfShhAYjTIRXXP5HymHQRIyj4D1pujc",chatRequestModel)
    }

    override fun getCurrentUser() = firebaseAuth.currentUser

    override fun getUserData(userId:String,callback: (Map<String,Any>?)->Unit){
        firebaseFirestore.collection("users").document(userId).get()
            .addOnSuccessListener { document->
                if (document != null){
                    callback(document.data)
                }else{
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }
}