package com.muratdayan.lessonline.presentation.features.main.home.following_posts

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muratdayan.lessonline.domain.model.firebasemodels.Post
import com.muratdayan.lessonline.presentation.features.main.home.PostListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class FollowingPostsViewModel @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
): ViewModel() {

    private val _followingPostList = MutableStateFlow<PostListState>(PostListState.Nothing)
    val followingPostList: StateFlow<PostListState> = _followingPostList.asStateFlow()

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()


    fun fetchFollowingPosts(){
        val currentUserId = firebaseAuth.currentUser?.uid ?: return

        firebaseFirestore.collection("users").document(currentUserId).get()
            .addOnSuccessListener {document->
                val followingList = document.get("following") as? List<String> ?: emptyList()
                if (followingList.isEmpty()){
                    _followingPostList.value = PostListState.Success(emptyList())
                    return@addOnSuccessListener
                }

                firebaseFirestore.collection("posts").whereIn("userId",followingList)
                    .get()
                    .addOnSuccessListener { querySnapshot->
                        val posts = querySnapshot.toObjects(Post::class.java)
                        _followingPostList.value = PostListState.Success(posts)
                    }
                    .addOnFailureListener { exception ->
                        _errorState.value = exception.localizedMessage
                    }
            }
            .addOnFailureListener { exception ->
                _errorState.value = exception.localizedMessage
            }
    }


}