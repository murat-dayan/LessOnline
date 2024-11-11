package com.muratdayan.chatbot.data.remote.interceptors

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class ApiKeyInterceptor(private val apiKey:String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()
        val requestWithApiKey: Request = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer ${apiKey}")
            .build()
        return chain.proceed(requestWithApiKey)
    }

}