package com.muratdayan.lessonline.presentation.features.auth.register

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.muratdayan.lessonline.domain.model.firebasemodels.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Nothing)
    val signUpState = _signUpState.asStateFlow()

    fun signUp(
        username: String,
        email: String,
        password: String
    ) {
        _signUpState.value = SignUpState.Loading
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result.user?.let { user ->
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build()

                        user.updateProfile(profileUpdates)
                            .addOnCompleteListener { profileTask ->
                                if (profileTask.isSuccessful) {
                                    val currentUser = firebaseAuth.currentUser
                                    currentUser?.let {
                                        val uid = it.uid
                                        val email = it.email
                                        val name = it.displayName
                                        createUserProfile(uid, email, name)
                                    }
                                } else {
                                    _signUpState.value =
                                        SignUpState.Error(profileTask.exception?.message)
                                }
                            }
                        return@addOnCompleteListener
                    }

                } else {
                    _signUpState.value = SignUpState.Error(task.exception?.message)
                }
            }
    }

    private fun createUserProfile(uid: String, email: String?, name: String?) {
        val userProfile = UserProfile(
            email = email ?: "",
            name = name ?: "",
            profilePhoto = "",
            isProfileComplete = false
        )

        firestore.collection("users").document(uid)
            .set(userProfile)
            .addOnSuccessListener {
                _signUpState.value = SignUpState.Success
            }
            .addOnFailureListener {e->
                _signUpState.value = SignUpState.Error(e.message)
            }
    }


}

sealed class SignUpState {
    object Nothing : SignUpState()
    object Loading : SignUpState()
    object Success : SignUpState()
    data class Error(val message: String? = null) : SignUpState()
}