package com.muratdayan.lessonline.presentation.features.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.muratdayan.lessonline.domain.model.firebasemodels.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore
): ViewModel() {

    private val _postList = MutableStateFlow<List<Post>>(emptyList())
    val postList:StateFlow<List<Post>> = _postList


    fun fetchPosts(){
        viewModelScope.launch {
            firebaseFirestore.collection("posts").get()
                .addOnSuccessListener {result->
                    val postList = result.toObjects(Post::class.java)
                    _postList.value = postList
                }
                .addOnFailureListener {exception->
                    exception.printStackTrace()
                    // Handle failure
                }
        }
    }

    fun updatePostInFirebase(post: Post){
        val postRef = firebaseFirestore.collection("posts").document(post.postId)
        postRef.update("likeCount",post.likeCount,"likedByUsers",post.likedByUsers)
            .addOnSuccessListener {
                fetchPosts()
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }


}