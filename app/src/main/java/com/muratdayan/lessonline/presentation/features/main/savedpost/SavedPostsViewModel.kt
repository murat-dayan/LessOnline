package com.muratdayan.lessonline.presentation.features.main.savedpost

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muratdayan.core.util.Result
import com.muratdayan.lessonline.domain.model.firebasemodels.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SavedPostsViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) :ViewModel(){

    private val _savedPostList = MutableStateFlow<Result<List<Post>>>(Result.Idle)
    val savedPostList: StateFlow<Result<List<Post>>> = _savedPostList.asStateFlow()


    fun fetchSavedPosts(){
        _savedPostList.value = Result.Loading
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null){
            val userID = currentUser.uid
            firebaseFirestore.collection("users").document(userID).get()
                .addOnSuccessListener { document->
                    val savedPostsIds =  document.get("savedPosts") as? List<String> ?: emptyList()
                    if (savedPostsIds.isNotEmpty()){
                        firebaseFirestore.collection("posts")
                            .whereIn("postId",savedPostsIds)
                            .get()
                            .addOnSuccessListener { result->
                                val postList = result.toObjects(Post::class.java)
                                _savedPostList.value = Result.Success(postList)
                            }
                            .addOnFailureListener { exception->
                                exception.printStackTrace()
                                _savedPostList.value = Result.Error(exception)
                            }
                    }else{
                        _savedPostList.value = Result.Success(emptyList())
                    }
                }
                .addOnFailureListener { exception->
                    _savedPostList.value = Result.Error(exception)
                }
        }
    }


}