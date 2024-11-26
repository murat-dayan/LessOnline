package com.muratdayan.chat.presentation.chathistory

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muratdayan.chat.data.model.ChatUserModel
import com.muratdayan.chat.data.repository.ChatRepository
import com.muratdayan.core.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatHistoryViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {


    private val _usersChattedList = MutableStateFlow<Result<List<ChatUserModel>>>(Result.Idle)
    val usersChattedList: StateFlow<Result<List<ChatUserModel>>> = _usersChattedList

    private val _deleteStatus = MutableStateFlow<Result<Boolean>>(Result.Idle)
    val deleteStatus: StateFlow<Result<Boolean>> = _deleteStatus.asStateFlow()

    init {
        loadChatParticipantsAndGetNames()
    }

    private fun loadChatParticipantsAndGetNames() {
        viewModelScope.launch {
            _usersChattedList.value = Result.Loading
            chatRepository.getChatParticipants().collect { userIds ->
                Log.d("ChatHistoryViewModel", "User IDs: $userIds")
                chatRepository.getUserProfiles(userIds).collect { userNames ->
                    Log.d("ChatHistoryViewModel", "User Names: $userNames")
                    _usersChattedList.value = Result.Success(userNames)
                }
            }
        }
    }

    fun deleteChat(chatId: String) {
        viewModelScope.launch {
            chatRepository.deleteChat(chatId).collect { result ->
                _deleteStatus.value = result
                if (result is Result.Success && result.data) {
                    loadChatParticipantsAndGetNames()
                }
            }
        }
    }




}