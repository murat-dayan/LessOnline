package com.muratdayan.lessonline.data.remote.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muratdayan.lessonline.core.Result
import com.muratdayan.lessonline.domain.model.firebasemodels.UserProfile
import com.muratdayan.lessonline.presentation.util.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
)
{

    fun getUserProfile(otherUserId:String?=null) : Flow<Result<UserProfile?>> = flow {
        emit(Result.Loading)

        val userId = otherUserId ?: firebaseAuth.currentUser?.uid

        if (userId == null){
            emit(Result.Error(NullPointerException("Current user is null")))
            return@flow
        }
        val userDocRef = firebaseFirestore.collection("users").document(userId)
        val document = userDocRef.get().await()

        if (document.exists()){
            val userProfile = document.toObject(UserProfile::class.java)
            emit(Result.Success(userProfile))
        }else{
            emit(Result.Success(null))
        }
    }.catch {exception->
        emit(Result.Error(exception))
    }

    fun createUserProfile(
        uid:String,
        email:String?,
        name:String?,
        bio:String? ="",
        role:UserRole = UserRole.STUDENT,
    ) : Flow<Result<Unit>> = flow {
        emit(Result.Loading)

        val userProfile = UserProfile(
            userId = uid,
            username = name ?: "",
            userEmail = email ?: "",
            bio = bio?:"",
            followers = emptyList(),
            following = emptyList(),
            profilePhotoUrl = "",
            postPhotoUrls = emptyList(),
            isProfileComplete = false,
            role = role
        )

        try {
            firebaseFirestore.collection("users").document(uid).set(userProfile).await()
            emit(Result.Success(Unit)) // Başarılı sonuç
        } catch (e: Exception) {
            emit(Result.Error(e)) // Hata durumu
        }
    }.catch { exception ->
        emit(Result.Error(exception))  // Hata oluşursa hatayı yay
    }

    fun checkUserRole(): Flow<Result<Boolean>> = flow{
        emit(Result.Loading)

        val userId = firebaseAuth.currentUser?.uid

        if (userId == null){
            emit(Result.Error(NullPointerException("Current user is null")))
            return@flow
        }

        val userDocRef = firebaseFirestore.collection("users").document(userId)
        val document = userDocRef.get().await()

        if (document.exists()){
            val userProfile = document.toObject(UserProfile::class.java)
            val isTeacher = userProfile?.role == UserRole.TEACHER
            emit(Result.Success(isTeacher))
        }else{
            emit(Result.Success(false))
        }
    }.catch { exception->
        emit(Result.Error(exception))
    }

    fun updatePostInFirebase(
        postId:String,
        fieldsToUpdate: Map<String,Any>
    ) : Flow<Result<Boolean>> = flow {
        try {
            emit(Result.Loading)
            val postRef = firebaseFirestore.collection("posts").document(postId)
            postRef.update(fieldsToUpdate).await()
            emit(Result.Success(true))
        }catch (e:Exception){
            emit(Result.Error(e))
        }
    }.flowOn(Dispatchers.IO).catch {
        emit(Result.Error(it))
    }
}