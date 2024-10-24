package com.muratdayan.lessonline.presentation.base

import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.muratdayan.lessonline.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseFragment: Fragment() {

    private var loadingDialog: LoadingDialogFragment? = null
    private var isLoadingDialogVisible = false

    fun showLoading() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialogFragment()
        }

        loadingDialog?.let {
            if (!it.isAdded && !isLoadingDialogVisible) {
                it.show(parentFragmentManager, "loading")
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
}