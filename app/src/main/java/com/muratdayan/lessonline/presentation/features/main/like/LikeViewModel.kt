package com.muratdayan.lessonline.presentation.features.main.like

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LikeViewModel @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore
) : ViewModel(){
}