package com.muratdayan.lessonline.presentation.features.main.profile.yourprofile

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muratdayan.lessonline.domain.model.firebasemodels.Post
import com.muratdayan.lessonline.domain.model.firebasemodels.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
): ViewModel()  {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile = _userProfile.asStateFlow()

    private val _userPosts = MutableStateFlow<List<Post>>(emptyList())
    val userPosts = _userPosts.asStateFlow()


    fun getUserProfile(){
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null){
            val uid = currentUser.uid
            val userDocRef = firebaseFirestore.collection("users").document(uid)

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
        }else{
            _userProfile.value = null
            Log.d("ProfileViewModel", "Current user is null")
        }
    }

    fun getPosts(){
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null){
            val uid = currentUser.uid

            firebaseFirestore.collection("posts")
                .whereEqualTo("userId",uid)
                .get()
                .addOnCompleteListener { documents->
                    val posts = documents.result.mapNotNull {
                        it.toObject(Post::class.java)
                    }
                    _userPosts.value = posts
                }
                .addOnFailureListener {
                    Log.d("ProfileViewModel", "Error getting posts", it)
                }
        }else{
            _userPosts.value = emptyList()
        }
    }



}

