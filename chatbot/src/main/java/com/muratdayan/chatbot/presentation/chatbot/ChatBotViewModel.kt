package com.muratdayan.chatbot.presentation.chatbot

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.muratdayan.chatbot.data.remote.repository.ChatRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatBotViewModel @Inject constructor(
    private val chatRepository: ChatRepositoryImpl
) : ViewModel(){


    private val _chatResponse = MutableLiveData<String>()
    val chatResponse: LiveData<String> get() = _chatResponse

    private val _currentUser = MutableLiveData<FirebaseUser?>()
    val currentUser: LiveData<FirebaseUser?> get() = _currentUser

    init {
        _currentUser.value = chatRepository.getCurrentUser()
    }

    fun sendMessageToChatBot(message: String) {
        val user = _currentUser.value
        if (user != null) {
            getIdToken(user){token->
                if (token != null){
                    Log.i("ChatBotViewModel",token)
                    viewModelScope.launch {
                        val response = chatRepository.getChatResponse(idToken = token, message = message)
                        if (response.isSuccessful){
                            val body = response.body()
                            if (body != null) {
                                val responseString = response.body()?.candidates?.get(0)?.content?.parts?.get(0)?.text
                                Log.i("ChatBotViewModel", "Response Body JSON: $responseString")
                                responseString?.let {
                                    _chatResponse.value = it
                                }
                            } else {
                                Log.e("ChatBotViewModel", "API yanıtı boş: $body")
                                _chatResponse.value = "API yanıtı boş"
                            }
                        }else{
                            val errorMessage = response.errorBody()?.string() ?: "Hata mesajı alınamadı"
                            Log.e("ChatBotViewModel", "Hata: $errorMessage")
                            _chatResponse.value = "Bir hata oluştu: $errorMessage"
                        }
                    }
                }else {
                    _chatResponse.value = "ID token alınamadı"
                }
            }
        }else {
            _chatResponse.value = "Kullanıcı giriş yapmamış"
        }

    }

    private fun getIdToken(user:FirebaseUser,onComplete: (String?)->Unit){
        user.getIdToken(true).addOnCompleteListener {task->
            if (task.isSuccessful){
                val idToken = task.result?.token
                onComplete(idToken)
            }else{
                onComplete(null)
            }
        }
    }



}