package com.muratdayan.lessonline.domain.model.firebasemodels

import java.util.UUID

data class Post(
    val postId: String = UUID.randomUUID().toString(),
    val userId: String ="",
    val username:String="",
    val userPhoto:String="",
    val photoUri: String="",
    val comment:String="",
    val timestamp:Any = System.currentTimeMillis()
)
