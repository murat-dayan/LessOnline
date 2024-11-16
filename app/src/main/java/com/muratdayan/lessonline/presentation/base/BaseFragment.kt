package com.muratdayan.lessonline.presentation.base

import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseFragment: Fragment() {

    private var loadingDialog: LoadingDialogFragment? = null
    private var errorDialog : ErrorDialogFragment?=null
    private var isLoadingDialogVisible = false

    fun showLoading() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialogFragment()
        }

        loadingDialog?.let {
            if (!it.isAdded && !isLoadingDialogVisible) {
                it.show(parentFragmentManager, "showLoading")
                isLoadingDialogVisible = true // Diyalog açıldığında flag'i güncelle
            }
        }
    }

    fun hideLoading() {
        loadingDialog?.let {
            if (it.isAdded) {
                it.dismiss()
                loadingDialog = null
                isLoadingDialogVisible = false // Diyalog kapatıldığında flag'i güncelle
            }
        }
    }

    fun showError(errorText: String) {
        if (errorDialog == null) {
            errorDialog = ErrorDialogFragment(errorText)
        }

        errorDialog?.let {
            if (!it.isAdded && !isLoadingDialogVisible) {
                it.show(parentFragmentManager, "ShowError")
            }
        }
    }

    fun showCustomAlertDialog(
        message:String,
        positiveText:String,
        negativeText:String,
        onPositiveClick:()->Unit,
        onNegativeClick:()->Unit
    ){
        val dialog = CustomAlertDialogFragment(message,positiveText,negativeText,onPositiveClick,onNegativeClick)
        dialog.show(parentFragmentManager,"customAlertDialog")
    }

    fun hideError() {
        errorDialog?.let {
            if (it.isAdded) {
                it.dismiss()
                errorDialog = null
            }
        }
    }
}