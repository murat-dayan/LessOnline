package com.muratdayan.lessonline.presentation.features.main.profile.settings

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.muratdayan.lessonline.presentation.features.auth.login.LoginState
import com.muratdayan.lessonline.presentation.util.PreferenceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val preferenceHelper: PreferenceHelper
) : ViewModel(){

    fun logout() {
        firebaseAuth.signOut()
        preferenceHelper.saveUserLoginStatus(false) // Çıkış yapınca oturumu kapat
    }



}