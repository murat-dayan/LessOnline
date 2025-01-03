package com.muratdayan.lessonline.domain.model.firebasemodels

import android.provider.ContactsContract.CommonDataKinds.Photo
import com.muratdayan.lessonline.presentation.util.UserRole

data class UserProfile(
    val userId: String = "", // Kullanıcı ID'si
    val username: String = "", // Kullanıcı adı
    val userEmail: String = "", // Kullanıcı E-posta
    val bio: String = "", // Kullanıcının biyografisi
    val followers: List<String> = emptyList(), // Takipçilerinin userId'leri
    val following: List<String> = emptyList(), // Takip ettiklerinin userId'leri
    val profilePhotoUrl: String = "", // Profil fotoğrafının URL'si (Firebase Storage URL)
    val postPhotoUrls: List<String> = emptyList(), // Kullanıcının paylaştığı fotoğrafların URL'leri
    val isProfileComplete: Boolean = false,
    val role: UserRole = UserRole.STUDENT,
    val savedPosts: List<String> = emptyList(),
    val isPremium: Boolean = false
)
