package com.muratdayan.lessonline.presentation.features.main.home.all_posts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muratdayan.core.util.connectivity.ConnectivityObserver
import com.muratdayan.lessonline.domain.model.firebasemodels.Post
import com.muratdayan.lessonline.presentation.features.main.home.PostListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AllPostsViewModel @Inject constructor(
    private val connectivityObserver: ConnectivityObserver,
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
): ViewModel() {

    val isConnected: Flow<Boolean?> = connectivityObserver
        .isConnected
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            null
        )

    private val _postList = MutableStateFlow<PostListState>(PostListState.Nothing)
    val postList: StateFlow<PostListState> = _postList.asStateFlow()

    fun fetchPosts() {
        _postList.value = PostListState.Loading
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null){
            val userRef = firebaseFirestore.collection("users").document(userId)
            userRef.get()
                .addOnSuccessListener { userSnapshot->
                    val savedPosts = userSnapshot.get("savedPosts") as? List<String> ?: emptyList()

                    firebaseFirestore.collection("posts").get()
                        .addOnSuccessListener { result ->
                            val postList = result.toObjects(Post::class.java).map { post->
                                post.isBookmarked = savedPosts.contains(post.postId)
                                post
                            }
                            _postList.value = if (postList.isNotEmpty()){
                                PostListState.Success(postList)
                            }else {
                                PostListState.Error("No Posts Foundd")
                            }
                        }
                        .addOnFailureListener { exception ->
                            _postList.value = PostListState.Error(exception.message)
                            exception.printStackTrace()
                            Log.e("HomeViewModel", "Error fetching posts: ${exception.message}")
                        }
                }
                .addOnFailureListener { exception->
                    Log.e("HomeViewModel","Error fetching userdataa: ${exception.message}")
                }
        }



    }
}