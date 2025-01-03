package com.muratdayan.lessonline.presentation.features.main.profile.yourprofile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.muratdayan.core.util.Result
import com.muratdayan.lessonline.data.remote.repository.FirebaseRepository
import com.muratdayan.lessonline.domain.model.firebasemodels.Post
import com.muratdayan.lessonline.domain.model.firebasemodels.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage,
    private val firebaseRepository: FirebaseRepository
): ViewModel()  {

    private val _userProfile = MutableStateFlow<Result<UserProfile?>>(Result.Idle)
    val userProfile = _userProfile.asStateFlow()

    private val _userPosts = MutableStateFlow<List<Post>>(emptyList())
    val userPosts = _userPosts.asStateFlow()

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState = _uploadState.asStateFlow()


    fun getUserProfile(){
        viewModelScope.launch {
            firebaseRepository.getUserProfile().collectLatest { result->
                _userProfile.value = result
            }
        }
    }

    fun getPosts(){
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null){
            val uid = currentUser.uid

            firebaseFirestore.collection("posts")
                .whereEqualTo("userId",uid)
                .get()
                .addOnCompleteListener { documents->
                    val posts = documents.result.mapNotNull {
                        it.toObject(Post::class.java)
                    }
                    _userPosts.value = posts
                }
                .addOnFailureListener {
                    Log.d("ProfileViewModel", "Error getting posts", it)
                }
        }else{
            _userPosts.value = emptyList()
        }
    }

    fun uploadProfilePhotoAndSaveUrl(uri: Uri){
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null){
            _uploadState.value = UploadState.Loading

            val fileRef = firebaseStorage.reference.child("profile_photos/${UUID.randomUUID()}.jpg")
            fileRef.putFile(uri)
                .addOnSuccessListener {
                    fileRef.downloadUrl.addOnSuccessListener { downloadUri->
                        updateProfilePhotoUrl(currentUser.uid,downloadUri.toString())
                    }
                }
                .addOnFailureListener { exception->
                    _uploadState.value = UploadState.Error("Failed to upload profile photo: ${exception.message}")
                }
        }else{
            _uploadState.value = UploadState.Error("Current user is null")
        }
    }

    private fun updateProfilePhotoUrl(uid: String, photoUrl: String){
        val userDocRef = firebaseFirestore.collection("users").document(uid)
        userDocRef.update("profilePhotoUrl",photoUrl)
            .addOnSuccessListener {
                _uploadState.value = UploadState.Success("Profile photo updated successfully", uri = photoUrl)
            }
            .addOnFailureListener {exception->
                _uploadState.value = UploadState.Error("Failed to update profile photo url: ${exception.message}")
            }
    }

    sealed class UploadState {
        object Idle : UploadState()
        object Loading : UploadState()
        data class Success(val message: String, val uri: String) : UploadState()
        data class Error(val error: String) : UploadState()
    }
}

