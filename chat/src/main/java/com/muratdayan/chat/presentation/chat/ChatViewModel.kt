package com.muratdayan.chat.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.muratdayan.chat.data.model.MessageModel
import com.muratdayan.chat.data.repository.ChatRepository
import com.muratdayan.core.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val firebaseAuth: FirebaseAuth
): ViewModel() {

    private val _chatIdState = MutableStateFlow<String?>(null)
    val chatIdState: StateFlow<String?> = _chatIdState

    private val _sendMessageStatus = MutableStateFlow<Boolean>(false)
    val sendMessageStatus: StateFlow<Boolean> = _sendMessageStatus

    private val _messagesState = MutableStateFlow<Result<List<MessageModel>>>(Result.Idle)
    val messagesState: StateFlow<Result<List<MessageModel>>> = _messagesState.asStateFlow()

    private val _receiverName = MutableStateFlow<String>("")
    val receiverName: StateFlow<String> = _receiverName


    fun initiateChat(targetUserId: String) {
        viewModelScope.launch {
            chatRepository.initiateChat(targetUserId).collect { chatId ->
                _chatIdState.value = chatId
                chatId?.let {
                    observeMessages(it)
                }
            }
        }
    }

    fun sendMessage(chatId: String, messageContent: String, targetUserId: String) {
        viewModelScope.launch {
            chatRepository.sendMessage(chatId, messageContent, targetUserId).collect { isSuccess ->
                _sendMessageStatus.value = isSuccess
            }
        }
    }

    fun checkViewType(senderId:String): Int{
        val currentUser = firebaseAuth.currentUser?.uid
        val resultType =currentUser?.let {
            if (it == senderId){
                 1
            }else{
                 2
            }
        }
        return  resultType ?: 1
    }

    fun observeMessages(chatId: String){
        _messagesState.value = Result.Loading
        viewModelScope.launch {
            chatRepository.getMessages(chatId).collect{messages->
                _messagesState.value = Result.Success(messages)
            }
        }
    }

    fun getReceiverName(receiverId:String){
        viewModelScope.launch {
            chatRepository.getReceiverName(receiverId).collectLatest { receiverName->
                if (receiverName != null) {
                    _receiverName.value = receiverName
                }
            }
        }
    }



}