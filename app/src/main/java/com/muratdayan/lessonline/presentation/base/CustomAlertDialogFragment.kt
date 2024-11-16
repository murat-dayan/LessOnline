package com.muratdayan.lessonline.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.muratdayan.lessonline.databinding.DialogFragmentCustomAlertDialogBinding

class CustomAlertDialogFragment(
    private val message:String,
    private val positiveText:String,
    private val negativeText:String,
    private val onPositiveClick:()->Unit,
    private val onNegativeClick:()->Unit
) : DialogFragment() {

    private var _binding : DialogFragmentCustomAlertDialogBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = DialogFragmentCustomAlertDialogBinding.inflate(inflater,container,false)

        binding.tvAlertMessage.text = message
        binding.tvPositive.text = positiveText
        binding.tvNegative.text = negativeText

        binding.tvPositive.setOnClickListener {
            onPositiveClick.invoke()
            dismiss()
        }

        binding.tvNegative.setOnClickListener {
            onNegativeClick.invoke()
            dismiss()
        }

        return  binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}