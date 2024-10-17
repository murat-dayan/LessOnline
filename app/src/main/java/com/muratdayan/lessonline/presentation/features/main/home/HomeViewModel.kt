package com.muratdayan.lessonline.presentation.features.main.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.muratdayan.lessonline.domain.model.firebasemodels.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _postList = MutableStateFlow<PostListState>(PostListState.Nothing)
    val postList: StateFlow<PostListState> = _postList.asStateFlow()


    fun fetchPosts() {
        _postList.value = PostListState.Loading
        viewModelScope.launch {
            firebaseFirestore.collection("posts").get()
                .addOnSuccessListener { result ->
                    val postList = result.toObjects(Post::class.java)
                    Log.d("HomeViewModel", "Fetched posts: $postList") // Log ekleyin
                    if (postList.isNotEmpty()) {
                        _postList.value = PostListState.Success(postList)
                    } else {
                        _postList.value = PostListState.Error("No posts found")
                    }
                }
                .addOnFailureListener { exception ->
                    _postList.value = PostListState.Error(exception.message)
                    exception.printStackTrace()
                    Log.e("HomeViewModel", "Error fetching posts: ${exception.message}")
                }
        }
    }

    fun updatePostInFirebase(post: Post) {
        val postRef = firebaseFirestore.collection("posts").document(post.postId)
        postRef.update("likeCount", post.likeCount, "likedByUsers", post.likedByUsers)
            .addOnSuccessListener {
                fetchPosts()
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                Log.e("HomeViewModel", "Error fetching posts: ${exception.message}")
            }
    }

    sealed class PostListState {
        object Nothing : PostListState()
        object Loading : PostListState()
        data class Success(val postList: List<Post>) : PostListState()
        data class Error(val message: String? = null) : PostListState()
    }
}