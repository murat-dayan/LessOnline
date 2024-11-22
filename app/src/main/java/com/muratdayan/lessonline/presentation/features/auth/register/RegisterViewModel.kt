package com.muratdayan.lessonline.presentation.features.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.muratdayan.core.util.Result
import com.muratdayan.lessonline.data.remote.repository.FirebaseRepository
import com.muratdayan.lessonline.presentation.util.PreferenceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val preferenceHelper: PreferenceHelper,
    private val firebaseRepository: FirebaseRepository
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
        viewModelScope.launch {
            firebaseRepository.createUserProfile(uid, email, name).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _signUpState.value = SignUpState.Loading
                    }
                    is Result.Success -> {
                        preferenceHelper.saveUserLoginStatus(true)
                        _signUpState.value = SignUpState.Success
                    }
                    is Result.Error -> {
                        _signUpState.value = SignUpState.Error(result.exception.message)
                    }

                    Result.Idle -> {}
                }
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