package com.muratdayan.lessonline.presentation.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.muratdayan.lessonline.core.Result
import com.muratdayan.lessonline.data.remote.repository.FirebaseRepository
import com.muratdayan.lessonline.presentation.util.PreferenceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferenceHelper: PreferenceHelper,
    private val firebaseRepository: FirebaseRepository,
    private val firebaseAuth: FirebaseAuth
) :ViewModel(){

    private val _isTeacher = MutableLiveData<Boolean>()
    val isTeacher: LiveData<Boolean> get() = _isTeacher

    fun checkUserRole(){
        viewModelScope.launch {
            firebaseRepository.checkUserRole().collectLatest {result->
                when(result){
                    is Result.Error -> {

                    }
                    Result.Idle -> {}
                    Result.Loading -> {}
                    is Result.Success -> {
                        _isTeacher.value = result.data
                    }
                }

            }
        }
    }

    fun checkAuthUser(){
        firebaseAuth.addAuthStateListener { auth->
            if (auth.currentUser != null){
                checkUserRole()
            }
            else{
                _isTeacher.value = false
            }
        }
    }


    fun isUserLoggedIn(): Boolean {
        return preferenceHelper.isUserLoggedIn()
    }

}