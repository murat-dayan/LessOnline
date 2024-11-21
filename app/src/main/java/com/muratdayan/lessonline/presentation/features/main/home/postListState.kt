package com.muratdayan.lessonline.presentation.features.main.home

import com.muratdayan.lessonline.domain.model.firebasemodels.Post

sealed class PostListState {
    object Nothing : PostListState()
    object Loading : PostListState()
    data class Success(val postList: List<Post>) : PostListState()
    data class Error(val message: String? = null) : PostListState()
}