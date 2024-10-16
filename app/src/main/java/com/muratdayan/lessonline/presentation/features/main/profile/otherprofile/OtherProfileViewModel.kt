package com.muratdayan.lessonline.presentation.features.main.profile.otherprofile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muratdayan.lessonline.domain.model.firebasemodels.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class OtherProfileViewModel @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) :ViewModel(){


    private val _isFollowing = MutableStateFlow(false)
    val isFollowing = _isFollowing.asStateFlow()

    private val _followMessage = MutableStateFlow<String?>(null)
    val followMessage: StateFlow<String?> = _followMessage.asStateFlow()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile = _userProfile.asStateFlow()

    private val currentUserId: String get() = firebaseAuth.currentUser?.uid?:""

    fun checkIfFollowing(otherUserId:String){
        viewModelScope.launch {
            try {
                val currentUserDoc = firebaseFirestore.collection("users")
                    .document(currentUserId).get().await()

                val followingList = currentUserDoc.get("following") as? List<*>
                _isFollowing.value = followingList?.contains(otherUserId)?:false
            }catch (e:Exception){
                _isFollowing.value = false
                _followMessage.value = "Error checking follow status ${e.message}"
            }
        }
    }

    fun followUser(otherUserId: String) {
        viewModelScope.launch {
            try {
                val currentUserDoc = firebaseFirestore.collection("users").document(currentUserId)
                val otherUserDoc = firebaseFirestore.collection("users").document(otherUserId)

                firebaseFirestore.runTransaction { transaction ->
                    transaction.update(
                        otherUserDoc,
                        "followers",
                        com.google.firebase.firestore.FieldValue.arrayUnion(currentUserId)
                    )
                    transaction.update(
                        currentUserDoc,
                        "following",
                        com.google.firebase.firestore.FieldValue.arrayUnion(otherUserId)
                    )
                }.await()
                getUserProfile(otherUserId)
                _isFollowing.value = true
                _followMessage.value = "You are now following this user"
            } catch (e: Exception) {
                _followMessage.value = "Error following"
            }
        }
    }

    fun unFollowUser(otherUserId: String){
        viewModelScope.launch {
            try {
                val currentUserDoc = firebaseFirestore.collection("users").document(currentUserId)
                val otherUserDoc = firebaseFirestore.collection("users").document(otherUserId)

                firebaseFirestore.runTransaction { transaction ->
                    transaction.update(
                        otherUserDoc,
                        "followers",
                        com.google.firebase.firestore.FieldValue.arrayRemove(currentUserId)
                    )
                    transaction.update(
                        currentUserDoc,
                        "following",
                        com.google.firebase.firestore.FieldValue.arrayRemove(otherUserId)
                    )
                }.await()
                getUserProfile(otherUserId)
                _isFollowing.value = false
                _followMessage.value = "You have unfollowed this user"
            }catch (e:Exception){
                _followMessage.value = "Error unfollowing"
            }
        }
    }

    fun getUserProfile(otherUserId: String){
            val userDocRef = firebaseFirestore.collection("users").document(otherUserId)

            userDocRef.get()
                .addOnSuccessListener {document->
                    if (document != null && document.exists()){
                        val userProfile = document.toObject(UserProfile::class.java)
                        _userProfile.value = userProfile
                    }else{
                        _userProfile.value = null
                        Log.d("ProfileViewModel", "User profile not found")
                    }
                }
                .addOnFailureListener {
                    _userProfile.value = null
                    Log.d("ProfileViewModel", "Error getting user profile", it)
                }
    }

}