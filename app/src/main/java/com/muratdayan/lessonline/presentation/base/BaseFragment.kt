package com.muratdayan.lessonline.presentation.base

import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseFragment: Fragment() {

    private var loadingDialog: LoadingDialogFragment? = null

    fun showLoading(){
        if (loadingDialog == null){
            loadingDialog = LoadingDialogFragment()
        }

        loadingDialog?.let{
            if (!it.isAdded){
                it.show(parentFragmentManager,"loading")
            }
        }
    }

    fun hideLoading(){
        loadingDialog?.let{
            if (it.isAdded){
                it.dismiss()
            }
        }
    }
}