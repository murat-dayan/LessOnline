package com.muratdayan.lessonline.presentation.features.main.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.AdRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.muratdayan.lessonline.core.Result
import com.muratdayan.lessonline.data.remote.repository.FirebaseRepository
import com.muratdayan.lessonline.domain.model.firebasemodels.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _postList = MutableStateFlow<PostListState>(PostListState.Nothing)
    val postList: StateFlow<PostListState> = _postList.asStateFlow()

    // AdRequest'i canlı veri olarak yönetmek için LiveData
    private val _adRequest = MutableLiveData<AdRequest>()
    val adRequest: LiveData<AdRequest> get() = _adRequest

    init {
        loadAd() // Reklamı başlat
    }

    private fun loadAd() {
        // AdRequest oluşturuluyor
        val request = AdRequest.Builder()
            .build()
        _adRequest.value = request
    }


    fun fetchPosts() {
        _postList.value = PostListState.Loading
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null){
            val userRef = firebaseFirestore.collection("users").document(userId)
            userRef.get()
                .addOnSuccessListener { userSnapshot->
                    val savedPosts = userSnapshot.get("savedPosts") as? List<String> ?: emptyList()

                    firebaseFirestore.collection("posts").get()
                        .addOnSuccessListener { result ->
                            val postList = result.toObjects(Post::class.java).map { post->
                                post.isBookmarked = savedPosts.contains(post.postId)
                                post
                            }
                            _postList.value = if (postList.isNotEmpty()){
                                PostListState.Success(postList)
                            }else {
                                PostListState.Error("No Posts Foundd")
                            }
                        }
                        .addOnFailureListener { exception ->
                            _postList.value = PostListState.Error(exception.message)
                            exception.printStackTrace()
                            Log.e("HomeViewModel", "Error fetching posts: ${exception.message}")
                        }
                }
                .addOnFailureListener { exception->
                    Log.e("HomeViewModel","Error fetching userdataa: ${exception.message}")
                }
        }



    }

    private fun updateLikesCount(postId: String,fieldsToUpdate:Map<String,Any>, onComplete: () -> Unit){
        viewModelScope.launch {
            firebaseRepository.updatePostInFirebase(postId,fieldsToUpdate).collect{result->
                when(result){
                    is Result.Error -> {}
                    Result.Idle -> {}
                    Result.Loading -> {}
                    is Result.Success -> {
                        onComplete.invoke()
                    }
                }
            }
        }
    }



    fun toggleBookmark(postId:String){
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null){
            val userRef = firebaseFirestore.collection("users").document(userId)
            val postRef = firebaseFirestore.collection("posts").document(postId)

            firebaseFirestore.runTransaction { transaction->
                val snapshot = transaction.get(userRef)
                val savedPosts = snapshot.get("savedPosts") as? List<String> ?: emptyList()

                if (savedPosts.contains(postId)){
                    transaction.update(postRef,"bookmarked",false)
                    transaction.update(userRef,"savedPosts",FieldValue.arrayRemove(postId))
                }else{
                    transaction.update(userRef,"savedPosts",FieldValue.arrayUnion(postId))
                    transaction.update(postRef,"bookmarked",true)
                }
            }
                .addOnSuccessListener {
                    fetchPosts()
                }
                .addOnFailureListener {e->
                    Log.e("HomeViewModel","Error Updating bookmark: ${e.message}")
                }
        }
    }

    fun isPostBookMarked(postId: String,onResult:(Boolean)->Unit){
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null){
            val userRef = firebaseFirestore.collection("users").document(userId)
            userRef.get()
                .addOnSuccessListener { document->
                    val savedPosts = document.get("savedPosts") as? List<String> ?: emptyList()
                    onResult(savedPosts.contains(postId))
                }
                .addOnFailureListener { e->
                    Log.e("HomeViewModel","Error fetching bookmark: ${e.message}")
                    onResult(false)
                }
        }
    }

    fun toggleLikePost(post: Post, onComplete: () -> Unit){
        val currentUserId = firebaseAuth.currentUser?.uid
        if (currentUserId != null) {
            // Kullanıcı, "likedByUsers" listesinde mevcut mu kontrol et
            if (post.likedByUsers.contains(currentUserId)) {
                // Kullanıcı daha önce beğenmiş, beğeniyi kaldır
                post.likedByUsers = post.likedByUsers.filter { it != currentUserId }
                post.likeCount--
            } else {
                // Kullanıcı daha önce beğenmemiş, beğeniyi ekle
                post.likedByUsers += currentUserId
                post.likeCount++
            }
            val fieldsToUpdate = mapOf("likeCount" to post.likeCount)
            updateLikesCount(post.postId,fieldsToUpdate){
                onComplete.invoke()
            }

        } else {
            // Kullanıcı oturum açmamış
        }
    }

    sealed class PostListState {
        object Nothing : PostListState()
        object Loading : PostListState()
        data class Success(val postList: List<Post>) : PostListState()
        data class Error(val message: String? = null) : PostListState()
    }
}