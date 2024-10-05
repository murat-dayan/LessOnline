package com.muratdayan.lessonline.presentation.features.auth.register

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
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
                                    _signUpState.value = SignUpState.Success
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


}

sealed class SignUpState {
    object Nothing : SignUpState()
    object Loading : SignUpState()
    object Success : SignUpState()
    data class Error(val message: String? = null) : SignUpState()
}