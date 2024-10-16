package com.muratdayan.lessonline.presentation.features.main.search

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
class SearchViewModel @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore
) : ViewModel(){

    private val _postList = MutableStateFlow<List<Post>>(emptyList())
    val postList: StateFlow<List<Post>> = _postList

    private val _searchedPostList = MutableStateFlow<List<Post>>(emptyList())
    val searchedPostList: StateFlow<List<Post>> = _searchedPostList

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

    fun searchPosts(query:String){
        viewModelScope.launch {
            firebaseFirestore.collection("posts")
                .get()
                .addOnSuccessListener {result->
                    val postList = result.toObjects(Post::class.java)
                    val filteredPost = postList.filter {post->
                        post.comment.contains(query, ignoreCase = true)
                    }
                    _searchedPostList.value = filteredPost
                }
                .addOnFailureListener { exception->
                    exception.printStackTrace()
                }
        }
    }


}