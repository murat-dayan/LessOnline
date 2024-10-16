package com.muratdayan.lessonline.presentation.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BaseViewModel: ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading


    fun showLoading(){
        _isLoading.value = true
    }

    fun hideLoading(){
        _isLoading.value = false
    }


}