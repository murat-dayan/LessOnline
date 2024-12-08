package com.muratdayan.lessonline.presentation.features.main.profile.info

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.muratdayan.core.util.Result
import com.muratdayan.lessonline.data.remote.repository.FirebaseRepository
import com.muratdayan.lessonline.presentation.features.main.profile.yourprofile.ProfileViewModel.UploadState
import com.muratdayan.lessonline.presentation.util.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class GetProfileInfoViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseRepository: FirebaseRepository,
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage,
) : ViewModel() {

    private val _saveState = MutableStateFlow<Result<Unit>>(Result.Idle)
    val saveState = _saveState.asStateFlow()

    private val _profilePhotouploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val profilePhotouploadState = _profilePhotouploadState.asStateFlow()


    fun createUserProfile(name: String, bio: String?, role: UserRole?) {

        val uid = firebaseAuth.currentUser?.uid ?: return
        val email = firebaseAuth.currentUser?.email ?: return
        val newRole = role ?: UserRole.STUDENT

        viewModelScope.launch {

            firebaseRepository.createUserProfile(
                uid, email = email, name = name, bio = bio, role = newRole
            ).collectLatest { result ->
                _saveState.value = result
            }

        }
    }

    fun uploadProfilePhotoAndSaveUrl(uri: Uri){
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null){
            _profilePhotouploadState.value = UploadState.Loading

            val fileRef = firebaseStorage.reference.child("profile_photos/${UUID.randomUUID()}.jpg")
            fileRef.putFile(uri)
                .addOnSuccessListener {
                    fileRef.downloadUrl.addOnSuccessListener { downloadUri->
                        updateProfilePhotoUrl(currentUser.uid,downloadUri.toString())
                    }
                }
                .addOnFailureListener { exception->
                    _profilePhotouploadState.value = UploadState.Error("Failed to upload profile photo: ${exception.message}")
                }
        }else{
            _profilePhotouploadState.value = UploadState.Error("Current user is null")
        }
    }

    private fun updateProfilePhotoUrl(uid: String, photoUrl: String){
        val userDocRef = firebaseFirestore.collection("users").document(uid)
        userDocRef.update("profilePhotoUrl",photoUrl)
            .addOnSuccessListener {
                _profilePhotouploadState.value = UploadState.Success("Profile photo updated successfully", uri = photoUrl)
            }
            .addOnFailureListener {exception->
                _profilePhotouploadState.value = UploadState.Error("Failed to update profile photo url: ${exception.message}")
            }
    }

    sealed class UploadState {
        data object Idle : UploadState()
        data object Loading : UploadState()
        data class Success(val message: String, val uri: String) : UploadState()
        data class Error(val error: String) : UploadState()
    }
}