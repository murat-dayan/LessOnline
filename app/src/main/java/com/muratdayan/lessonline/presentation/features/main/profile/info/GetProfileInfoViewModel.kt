package com.muratdayan.lessonline.presentation.features.main.profile.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muratdayan.lessonline.core.Result
import com.muratdayan.lessonline.data.remote.repository.FirebaseRepository
import com.muratdayan.lessonline.presentation.util.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetProfileInfoViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _saveState = MutableStateFlow<Result<Unit>>(Result.Idle)
    val saveState = _saveState.asStateFlow()


    fun createUserProfile(name: String, bio: String?, role: UserRole?) {

        val uid = firebaseAuth.currentUser?.uid ?: return
        val email = firebaseAuth.currentUser?.email ?: return
        val newRole = role ?: UserRole.STUDENT

        viewModelScope.launch {

            firebaseRepository.createUserProfile(
                uid, email = email, name = name, bio = bio, role = newRole
            ).collectLatest { result ->
                _saveState.value = result
            }

        }
    }
}