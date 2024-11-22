package com.muratdayan.lessonline.presentation.features.auth.login

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.muratdayan.lessonline.R
import com.muratdayan.lessonline.presentation.util.PreferenceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    private val preferenceHelper:PreferenceHelper
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
                        preferenceHelper.saveUserLoginStatus(true)
                        _loginState.value = LoginState.Success()
                        return@addOnCompleteListener
                    }
                    _loginState.value = LoginState.Error(task.exception)
                }else{
                    _loginState.value = LoginState.Error(task.exception)
                }
            }
    }

    private lateinit var googleSignInClient: GoogleSignInClient

    fun initializeGoogleSignInClient(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context,gso)
    }

    fun loginWithGoogle(activityResultLauncher: ActivityResultLauncher<Intent>){
        val signInIntent = googleSignInClient.signInIntent
        activityResultLauncher.launch(signInIntent)
    }

    fun handleGoogleSignInResult(data:Intent?){
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        if (task.isSuccessful){
            val account = task.result
            account?.let {
                val credential = GoogleAuthProvider.getCredential(it.idToken,null)
                signInWithFirebase(credential)
            }
        }else{
            _loginState.value = LoginState.Error(task.exception)
        }
    }

    private fun signInWithFirebase(credential: AuthCredential){
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener {task->
                if (task.isSuccessful){
                    task.result.user?.let {user->
                        checkIfUserProfileExists(userId = user.uid)
                    }
                }else{
                    _loginState.value = LoginState.Error(task.exception)
                }
            }
    }

    private fun checkIfUserProfileExists(userId:String){
        firebaseFirestore.collection("users").document(userId).get()
            .addOnSuccessListener { document->
                if (document.exists()){
                    preferenceHelper.saveUserLoginStatus(true)
                    _loginState.value = LoginState.Success(isGoogleLogin = true, isNewUser = false)
                }else{
                    preferenceHelper.saveUserLoginStatus(true)
                    _loginState.value = LoginState.Success(true, isNewUser = true)
                }
            }
            .addOnFailureListener {exc->
                _loginState.value = LoginState.Error(exc)
            }
    }
    fun checkIfUserIsLoggedIn(){
        if (preferenceHelper.isUserLoggedIn()){
            _loginState.value = LoginState.Success()
        }
    }
}

sealed class LoginState{
    object Nothing: LoginState()
    object Loading: LoginState()
    data class Success(val isGoogleLogin: Boolean= false, val isNewUser:Boolean = false): LoginState()
    data class Error(val exception: Exception?=null): LoginState()

}