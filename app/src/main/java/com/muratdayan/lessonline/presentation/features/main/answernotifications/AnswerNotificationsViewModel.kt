package com.muratdayan.lessonline.presentation.features.main.answernotifications

import android.util.Log
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
                    markNotificationsAsRead()
                    NotificationUIState.Success(notificationList)
                }
            }

    }

    private fun markNotificationsAsRead(){
        val userId = firebaseAuth.currentUser?.uid ?: return

        val notificationsRef = firebaseFirestore.collection("users").document(userId)
            .collection("notifications")

        notificationsRef.get()
            .addOnSuccessListener { snapshot ->
                val batch = firebaseFirestore.batch()
                snapshot.documents.forEach { document ->
                    val docRef = notificationsRef.document(document.id)
                    batch.update(docRef, "read", true)
                }
                batch.commit().addOnSuccessListener {
                    Log.d("AnswerNotificationsViewModel", "Notifications marked as read")
                }.addOnFailureListener { e ->
                    Log.e("AnswerNotificationsViewModel", "Error marking notifications as read", e)
                }

            }
    }

    sealed class NotificationUIState {
        data object Loading : NotificationUIState()  // Yükleniyor durumu
        data class Success(val notifications: List<NotificationModel>) : NotificationUIState()  // Başarı durumu, liste dolu
        data class Error(val message: String) : NotificationUIState()  // Hata durumu
        data object Empty : NotificationUIState() // Bildirim yoksa
    }
}