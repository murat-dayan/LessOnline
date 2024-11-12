package com.muratdayan.chat.presentation.chathistory

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muratdayan.chat.data.model.ChatUserModel
import com.muratdayan.chat.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatHistoryViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {


    private val _userState = MutableStateFlow<List<ChatUserModel>>(emptyList())
    val userState: StateFlow<List<ChatUserModel>> = _userState

    init {
        loadChatParticipiantsAndGetNames()
    }

    private fun loadChatParticipiantsAndGetNames() {
        viewModelScope.launch {
            chatRepository.getChatParticipants().collect { userIds ->
                chatRepository.getUserProfiles(userIds).collect { userNames ->
                    Log.d("ChatHistoryViewModel", "User Names: $userNames")
                    _userState.value = userNames
                }
            }
        }
    }


}