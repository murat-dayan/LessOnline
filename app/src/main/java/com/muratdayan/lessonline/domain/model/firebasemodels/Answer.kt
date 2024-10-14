package com.muratdayan.lessonline.domain.model.firebasemodels

import com.google.firebase.Timestamp
import java.util.UUID

data class Answer(
    val answerId : String= UUID.randomUUID().toString(),
    val userId:String="",
    val postId:String="",
    val username:String="",
    val answer:String="",
    val timestamp: Any = System.currentTimeMillis()
)
