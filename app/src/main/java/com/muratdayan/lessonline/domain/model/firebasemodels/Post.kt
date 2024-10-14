package com.muratdayan.lessonline.domain.model.firebasemodels

import java.util.UUID

data class Post(
    val postId: String = UUID.randomUUID().toString(),
    val userId: String ="",
    val username:String="",
    val userPhoto:String="",
    val photoUri: String="",
    val comment:String="",
    var likeCount:Int =0,
    val answerCount:Int=0,
    var likedByUsers: List<String> = emptyList(),
    val timestamp:Any = System.currentTimeMillis()
)
