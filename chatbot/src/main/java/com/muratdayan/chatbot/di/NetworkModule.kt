package com.muratdayan.chatbot.di

import com.muratdayan.chatbot.data.remote.interceptors.ApiKeyInterceptor
import com.muratdayan.chatbot.data.remote.repository.ChatApiService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideApiKey(): String {
        return "AIzaSyAvAfShhAYjTIRXXP5HymHQRIyj4D1pujc"
    }

    @Provides
    fun provideOkHttpClient(apiKey: String): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor(apiKey)) // Interceptor'ı ekle
            .build()
    }

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/") // API URL'nizi buraya ekleyin
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideApiService(retrofit: Retrofit): ChatApiService {
        return retrofit.create(ChatApiService::class.java)
    }




}