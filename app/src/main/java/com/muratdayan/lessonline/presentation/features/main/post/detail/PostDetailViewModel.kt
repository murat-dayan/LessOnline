package com.muratdayan.lessonline.presentation.features.main.post.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muratdayan.lessonline.domain.model.firebasemodels.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) :ViewModel(){

    private val _post = MutableStateFlow<Post?>(null)
    val post = _post


    fun getPostById(postId:String){
        firebaseFirestore.collection("posts").document(postId).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()&& documentSnapshot !=null){
                    val post = documentSnapshot.toObject(Post::class.java)
                    _post.value = post
                }else{
                    post.value = null
                }
            }
            .addOnFailureListener { exception ->
                post.value = null
                Log.e("PostDetailViewModel", "Error getting post by ID", exception)
            }
    }

    fun updatePostInFirebase(post: Post,onResult:(Boolean)->Unit) {
        val postRef = firebaseFirestore.collection("posts").document(post.postId)
        postRef.update("likeCount", post.likeCount, "likedByUsers", post.likedByUsers)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener { exception ->
                onResult(false)
                exception.printStackTrace()
                Log.e("HomeViewModel", "Error fetching posts: ${exception.message}")
            }
    }


    fun toggleLike(post: Post, onSuccess: () -> Unit, onFailure: (Exception) -> Unit){
        val currentUserId = firebaseAuth.currentUser?.uid
        if (currentUserId != null){
            if (post.likedByUsers.contains(currentUserId)){

            }
        }
    }


}