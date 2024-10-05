package com.muratdayan.lessonline.presentation.features.auth.login

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Nothing)
    val loginState = _loginState.asStateFlow()


    fun login(
        email: String,
        password: String
    ){
        _loginState.value = LoginState.Loading

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {task->
                if (task.isSuccessful){
                    task.result.user?.let {
                        _loginState.value = LoginState.Success
                        return@addOnCompleteListener
                    }
                    _loginState.value = LoginState.Error
                }else{
                    _loginState.value = LoginState.Error
                }
            }

    }


}

sealed class LoginState{
    object Nothing: LoginState()
    object Loading: LoginState()
    object Success: LoginState()
    object Error: LoginState()

}