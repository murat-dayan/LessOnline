package com.muratdayan.chat.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muratdayan.chat.data.repository.ChatRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {

    @Provides
    fun provideChatRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): ChatRepository {
        return ChatRepository(firebaseAuth, firestore)
    }
}