package com.muratdayan.lessonline.presentation.features.main.post.edit

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.muratdayan.lessonline.domain.model.firebasemodels.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EditPostViewModel @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) : ViewModel() {

    private val _uploadState = MutableStateFlow<UploadAndSaveState>(UploadAndSaveState.Nothing)
    val uploadState = _uploadState.asStateFlow()

    fun uploadPhotoToFirebaseStorage(uri: Uri) {
        _uploadState.value = UploadAndSaveState.Loading
        val storageRef = firebaseStorage.reference.child("post_images/${UUID.randomUUID()}")

        storageRef.putFile(uri)
            .addOnSuccessListener { task ->
                storageRef.downloadUrl
                    .addOnSuccessListener { downloadUri ->
                        _uploadState.value = UploadAndSaveState.Success(downloadUri.toString())
                    }.addOnFailureListener {
                        _uploadState.value = UploadAndSaveState.Error(it.message)
                    }
            }.addOnFailureListener {
                _uploadState.value = UploadAndSaveState.Error(it.message)
            }
    }

    fun savePostToFirebase(
        photoUri: String,
        comment: String,
        postAnswers: List<String>,
        onPostSaved: () -> Unit,
    ) {
        _uploadState.value = UploadAndSaveState.Loading
        val user = firebaseAuth.currentUser

        user?.let { currentUser ->

            firebaseFirestore.collection("users").document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    val username = document.getString("username")
                    val userPhoto = document.getString("profilePhotoUrl")
                    val post = Post(
                        userId = user.uid,
                        username = username ?: "",
                        userPhoto = userPhoto ?: "",
                        photoUri = photoUri,
                        comment = comment,
                        postAnswers = postAnswers,
                        timestamp = System.currentTimeMillis()
                    )

                    firebaseFirestore.collection("posts")
                        .add(post)
                        .addOnSuccessListener { documentReference ->

                            val updatedPost = post.copy(postId = documentReference.id)
                            firebaseFirestore.collection("posts").document(documentReference.id)
                                .set(updatedPost)
                                .addOnSuccessListener {
                                    _uploadState.value = UploadAndSaveState.Success()
                                    onPostSaved()
                                }
                                .addOnFailureListener {
                                    _uploadState.value = UploadAndSaveState.Error(it.message)
                                }
                        }
                        .addOnFailureListener {
                            _uploadState.value = UploadAndSaveState.Error(it.message)
                        }

                }
        }
    }
}


sealed class UploadAndSaveState {
    object Nothing : UploadAndSaveState()
    object Loading : UploadAndSaveState()
    data class Success(val downloadUri: String? = null) : UploadAndSaveState()
    data class Error(val message: String? = null) : UploadAndSaveState()

}