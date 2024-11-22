package com.muratdayan.lessonline.presentation.features.main.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.muratdayan.core.util.Result
import com.muratdayan.lessonline.domain.model.firebasemodels.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore
) : ViewModel(){

    private val _postList = MutableStateFlow<Result<List<Post>>>(Result.Idle)
    val postList: StateFlow<Result<List<Post>>> = _postList.asStateFlow()

    private val _searchedPostList = MutableStateFlow<List<Post>>(emptyList())
    val searchedPostList: StateFlow<List<Post>> = _searchedPostList

    fun fetchPosts(){
        viewModelScope.launch {
            _postList.value = Result.Loading
            firebaseFirestore.collection("posts").get()
                .addOnSuccessListener {result->
                    val postList = result.toObjects(Post::class.java)
                    _postList.value = Result.Success(postList)
                }
                .addOnFailureListener {exception->
                    exception.printStackTrace()
                    _postList.value = Result.Error(exception)
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