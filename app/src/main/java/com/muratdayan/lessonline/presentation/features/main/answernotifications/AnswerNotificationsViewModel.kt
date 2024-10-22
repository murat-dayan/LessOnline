package com.muratdayan.lessonline.presentation.features.main.answernotifications

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muratdayan.lessonline.domain.model.firebasemodels.NotificationModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AnswerNotificationsViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) : ViewModel(){


    private val _uiState = MutableStateFlow<NotificationUIState>(NotificationUIState.Loading)
    val uiState: StateFlow<NotificationUIState> = _uiState.asStateFlow()

    fun loadNotifications() {
        _uiState.value = NotificationUIState.Loading
        val userId = firebaseAuth.currentUser?.uid ?: return
        firebaseFirestore.collection("users").document(userId)
            .collection("notifications")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    _uiState.value = NotificationUIState.Error(exception.message ?: "Unknown error")
                    return@addSnapshotListener
                }

                val notificationList = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(NotificationModel::class.java)
                } ?: emptyList()

                _uiState.value = if (notificationList.isEmpty()) {
                    NotificationUIState.Empty
                } else {
                    NotificationUIState.Success(notificationList)
                }
            }

    }

    sealed class NotificationUIState {
        object Loading : NotificationUIState()  // Yükleniyor durumu
        data class Success(val notifications: List<NotificationModel>) : NotificationUIState()  // Başarı durumu, liste dolu
        data class Error(val message: String) : NotificationUIState()  // Hata durumu
        object Empty : NotificationUIState() // Bildirim yoksa
    }
}