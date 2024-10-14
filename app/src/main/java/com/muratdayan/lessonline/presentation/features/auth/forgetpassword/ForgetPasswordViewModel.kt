package com.muratdayan.lessonline.presentation.features.auth.forgetpassword

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ForgetPasswordViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel(){

    private val _resetResult = MutableStateFlow<Boolean>(false)
    val resetResult = _resetResult.asStateFlow()

    fun resetPassword(email:String){
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener {task->
               if (task.isSuccessful){
                   _resetResult.value = true
               }
            }.addOnFailureListener {
                _resetResult.value = false
            }
    }

}