package com.muratdayan.lessonline.presentation

import androidx.lifecycle.ViewModel
import com.muratdayan.lessonline.presentation.util.PreferenceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferenceHelper: PreferenceHelper
) :ViewModel(){


    fun isUserLoggedIn(): Boolean {
        return preferenceHelper.isUserLoggedIn()
    }

}