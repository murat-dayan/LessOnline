package com.muratdayan.lessonline.domain.model.firebasemodels

import android.provider.ContactsContract.CommonDataKinds.Photo

data class UserProfile(
    val email:String = "",
    val name:String ="",
    val profilePhoto: String ="",
    val isProfileComplete: Boolean = false,
)
