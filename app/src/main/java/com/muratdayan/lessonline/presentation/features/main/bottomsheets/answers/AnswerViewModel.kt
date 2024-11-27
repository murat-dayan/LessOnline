package com.muratdayan.lessonline.presentation.features.main.bottomsheets.answers

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muratdayan.core.util.Result
import com.muratdayan.lessonline.data.remote.repository.FirebaseRepository
import com.muratdayan.lessonline.domain.model.firebasemodels.Answer
import com.muratdayan.lessonline.domain.model.firebasemodels.NotificationModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AnswerViewModel @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseRepository: FirebaseRepository
) :ViewModel(){

    private val _answers = MutableStateFlow<List<Answer>>(emptyList())
    val answers: StateFlow<List<Answer>> = _answers.asStateFlow()

    private val _addResult = MutableStateFlow<Boolean>(false)
    val addResult : StateFlow<Boolean> = _addResult.asStateFlow()


    private val _postAnswers = MutableStateFlow<List<String>?>(null)
    val postAnswers : StateFlow<List<String>?> = _postAnswers.asStateFlow()

    private val _isPostOwner = MutableLiveData<Boolean>()
    val isPostOwner: LiveData<Boolean> = _isPostOwner

    private val _isAnswerCountChanged = MutableStateFlow<Boolean>(false)
    val isAnswerCountChanged : StateFlow<Boolean> = _isAnswerCountChanged.asStateFlow()

    fun checkIfPostOwner(postId: String){
        val currentUserId = firebaseAuth.currentUser?.uid
        if (currentUserId == null){
            _isPostOwner.value = false
            return
        }
        firebaseFirestore.collection("posts").document(postId).get()
            .addOnSuccessListener { document->
                val postOwnerId = document.getString("userId")
                _isPostOwner.value = postOwnerId == currentUserId
            }
            .addOnFailureListener {
                _isPostOwner.value = false
            }
    }


    fun getAnswers(postId:String){
        val answersRef = firebaseFirestore.collection("posts").document(postId)
            .collection("answers")

        answersRef.addSnapshotListener { snapShot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            val answerList = snapShot?.documents?.mapNotNull {
                it.toObject(Answer::class.java)
            } ?: emptyList()

            _answers.value = answerList
        }
    }

    fun getPostAnswers(postId: String){
        firebaseFirestore.collection("posts").document(postId)
            .get()
            .addOnSuccessListener { document->
                if (document != null && document.exists()){
                    val postAnswers = document.get("postAnswers") as? List<String>
                    _postAnswers.value = postAnswers
                }else{
                    _postAnswers.value = emptyList()
                }
            }
            .addOnFailureListener {
                _postAnswers.value = emptyList()
            }
    }

    private fun canUserAddAnswer(postId: String, onResult:(Boolean)->Unit){
        val userId = firebaseAuth.currentUser!!.uid
        val answersRef = firebaseFirestore.collection("posts").document(postId)
            .collection("answers")
            .whereEqualTo("userId",userId)

        answersRef.get()
            .addOnSuccessListener { querySnapshot->
                if (querySnapshot.isEmpty){
                    onResult(true)
                }else{
                    onResult(false)
                }
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    private fun updateAnswersCount(postId: String){
        viewModelScope.launch {
            val postRef = firebaseFirestore.collection("posts").document(postId).get().await()
            val currentCount = postRef.getLong("answerCount") ?: 0
            val fieldsToUpdate = mapOf("answerCount" to currentCount+1)
            firebaseRepository.updatePostInFirebase(postId,fieldsToUpdate).collect{result->
                when(result){
                    is Result.Error -> {
                        Log.d("AnswerViewModel",result.exception.message.toString())
                        _isAnswerCountChanged.value = false
                    }
                    Result.Idle -> {}
                    Result.Loading -> {}
                    is Result.Success -> {
                        _isAnswerCountChanged.value = result.data
                    }
                }
            }
        }
    }



    fun addAnswer(postId: String,answerText:String){
        firebaseAuth.currentUser?.let {currentUser->
            canUserAddAnswer(postId){canAdd->
                if (canAdd){
                    val answer = Answer(
                        postId = postId,
                        answer = answerText,
                        userId = currentUser.uid,
                        username = currentUser.displayName.toString(),
                    )

                    firebaseFirestore.collection("posts").document(postId)
                        .collection("answers")
                        .add(answer)
                        .addOnSuccessListener {
                            _addResult.value = true
                            updateAnswersCount(postId)
                            sendNotificationToPostOwner(postId,currentUser.uid)
                        }
                        .addOnFailureListener {
                            _addResult.value = false
                        }
                }else{
                    _addResult.value = false
                }
            }
        }
    }

    private fun sendNotificationToPostOwner(postId: String,commenterId:String){
        firebaseFirestore.collection("posts").document(postId).get()
            .addOnSuccessListener { document->
                val postOwnerId = document.getString("userId")
                if (postOwnerId != null && postOwnerId != commenterId){
                    val noticationData = NotificationModel(
                        postId = postId,
                        commenterId = commenterId,
                        timestamp = System.currentTimeMillis(),
                        isRead = false,
                        commenterName = firebaseAuth.currentUser?.displayName.toString()
                    )

                    firebaseFirestore.collection("users").document(postOwnerId)
                        .collection("notifications")
                        .add(noticationData)
                }
            }
    }

}