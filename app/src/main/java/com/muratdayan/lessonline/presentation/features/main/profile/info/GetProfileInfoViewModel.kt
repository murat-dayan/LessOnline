package com.muratdayan.lessonline.presentation.features.main.profile.info

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muratdayan.lessonline.domain.model.firebasemodels.UserProfile
import com.muratdayan.lessonline.presentation.features.auth.register.SignUpState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class GetProfileInfoViewModel @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ViewModel(){

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Nothing)
    val saveState = _saveState.asStateFlow()


    fun createUserProfile(name: String, bio:String?,role:String?) {

        val uid = firebaseAuth.currentUser?.uid ?: return
        val email = firebaseAuth.currentUser?.email ?: return

        _saveState.value = SaveState.Loading

        firebaseAuth.currentUser?.let { user ->
            val userProfile =
                UserProfile(
                    userId = user.uid,
                    username = name,
                    userEmail = email,
                    bio = bio ?: "",
                    followers = emptyList(),
                    following = emptyList(),
                    profilePhotoUrl = "",
                    postPhotoUrls = emptyList(),
                    isProfileComplete = false,
                    role = role ?: "Student"
                )


            firebaseFirestore.collection("users").document(uid)
                .set(userProfile)
                .addOnSuccessListener {
                    _saveState.value = SaveState.Success
                }
                .addOnFailureListener { e ->
                    _saveState.value = SaveState.Error(e.message)
                }
        }
    }
}

sealed class SaveState {
    object Nothing : SaveState()
    object Loading : SaveState()
    object Success : SaveState()
    data class Error(val message: String? = null) : SaveState()
}