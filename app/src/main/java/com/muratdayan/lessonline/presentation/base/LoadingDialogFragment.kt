package com.muratdayan.lessonline.presentation.base

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.muratdayan.lessonline.R
import com.muratdayan.lessonline.databinding.FragmentLoadingDialogBinding


class LoadingDialogFragment : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.fragment_loading_dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

}