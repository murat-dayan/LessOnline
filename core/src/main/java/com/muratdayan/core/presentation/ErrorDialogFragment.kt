package com.muratdayan.core.presentation

import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.muratdayan.core.R

class ErrorDialogFragment(
    private val errorText:String
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.fragment_error_dialog)

        val tvError : TextView = dialog.findViewById(R.id.tv_error)
        tvError.text = errorText

        dialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(R.color.md_theme_onError)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }
}