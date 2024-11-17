package com.muratdayan.core.presentation

import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.muratdayan.core.R
import com.muratdayan.core.databinding.CustomToastBinding
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

    fun showToast(
        message: String,
        isError: Boolean
    ){
        val inflater: LayoutInflater = layoutInflater
        val binding: CustomToastBinding =
            CustomToastBinding.inflate(inflater)

        binding.tvCustomToastMessage.text = message
        if(isError){
            binding.ivCustomToastIcon.setImageResource(R.drawable.ic_error)
        }else{
            binding.ivCustomToastIcon.setImageResource(R.drawable.ic_info_outline)
        }

        with(Toast(requireContext())){
            duration = Toast.LENGTH_SHORT
            view = binding.root
            setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 100)
            show()
        }
    }
}