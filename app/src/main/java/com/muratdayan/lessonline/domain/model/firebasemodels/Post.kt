package com.muratdayan.lessonline.domain.model.firebasemodels

data class Post(
    val userId: String,
    val photoUri: String,
    val comment:String,
    val timestamp:Any
)
