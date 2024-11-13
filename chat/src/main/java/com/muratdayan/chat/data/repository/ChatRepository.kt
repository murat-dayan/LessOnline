package com.muratdayan.chat.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muratdayan.chat.data.model.ChatModel
import com.muratdayan.chat.data.model.ChatUserModel
import com.muratdayan.chat.data.model.MessageModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    fun initiateChat(targetUserId: String): Flow<String?> = flow {
        val currentUserId = firebaseAuth.currentUser?.uid ?: run {
            emit(null)
            return@flow
        }

        val chatId = if (currentUserId < targetUserId) {
            "$currentUserId-$targetUserId"
        } else {
            "$targetUserId-$currentUserId"
        }

        val chatData = ChatModel(
            chatId = chatId,
            participiants = listOf(currentUserId, targetUserId),
            createdAt = Timestamp.now()
        )

        try {
            firestore.collection("chats").document(chatId).set(chatData).await()
            emit(chatId)
        } catch (e: Exception) {
            emit(null)
        }
    }

    fun sendMessage(chatId: String, messageContent: String, targetUserId: String): Flow<Boolean> =
        flow {
            val currentUserId = firebaseAuth.currentUser?.uid ?: run {
                emit(false)
                return@flow
            }

            val messageId = firestore.collection("chats").document(chatId)
                .collection("messages").document().id

            val messageData = MessageModel(
                messageId = messageId,
                receiverId = targetUserId,
                senderId = currentUserId,
                content = messageContent,
                timestamp = Timestamp.now()
            )

            try {
                firestore.collection("chats").document(chatId).collection("messages")
                    .document(messageId).set(messageData).await()
                emit(true)
            } catch (e: Exception) {
                emit(false)
            }
        }

    fun getMessages(chatId: String): Flow<List<MessageModel>> = callbackFlow {
        val listenerRegistration = firestore.collection("chats").document(chatId)
            .collection("messages")
            .orderBy("timestamp")  // Mesajları zamana göre sıralamak için
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)  // Hata durumunda akışı kapat
                    return@addSnapshotListener
                }

                val messages = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(MessageModel::class.java)
                } ?: emptyList()

                trySend(messages)  // Yeni mesaj listesi her güncellemede yayınlanır
            }

        awaitClose { listenerRegistration.remove() }  // Dinleyiciyi kapat
    }

    fun getChatParticipants(): Flow<List<String>> = flow {
        val currentUserId = firebaseAuth.currentUser?.uid ?: run {
            emit(emptyList())
            return@flow
        }

        try {
            val querySnapshot = firestore.collection("chats")
                .whereArrayContains("participiants", currentUserId)
                .get()
                .await()

            val participantIds = querySnapshot.documents.flatMap { document ->
                val chat = document.toObject(ChatModel::class.java)
                chat?.participiants?.filter { it != currentUserId } ?: emptyList()
            }.distinct() // Aynı kullanıcıyla birden fazla chat varsa tekrar etmesini önler

            emit(participantIds)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    fun getUserProfiles(userIds: List<String>): Flow<List<ChatUserModel>> = flow {
        try {
            val userNames = userIds.map { userId ->
                val userSnapshot = firestore.collection("users").document(userId).get().await()
                val name =userSnapshot.getString("username")
                val id =userSnapshot.getString("userId")
                ChatUserModel(id!!,name!!)
            }
            emit(userNames)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    fun getReceiverName(receiverId:String): Flow<String?> = flow {
        try {
            val userDoc = firestore.collection("users").document(receiverId).get().await()
            val receiverName = userDoc.get("username").toString()
            emit(receiverName)
        }catch (e:Exception){
            emit(null)
        }
    }


}