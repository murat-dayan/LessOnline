package com.muratdayan.lessonline.domain.model.firebasemodels

data class NotificationModel(
    val type: String="comment",
    val postId: String="",
    val commenterId: String="",
    val commenterName: String="",
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
