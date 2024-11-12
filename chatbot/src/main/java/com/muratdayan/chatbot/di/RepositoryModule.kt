package com.muratdayan.chatbot.di

import com.muratdayan.chatbot.data.remote.repository.ChatRepositoryImpl
import com.muratdayan.chatbot.domain.repository.IChatRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindChatRepository(chatRepository: ChatRepositoryImpl): IChatRepository

}