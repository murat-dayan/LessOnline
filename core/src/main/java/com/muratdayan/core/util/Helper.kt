package com.muratdayan.core.util

import android.content.Context
import android.text.InputType
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.muratdayan.core.R

fun EditText.doIfIsEmptyAndReturn(onEmpty: () -> Unit): Boolean {
    val text = this.text.toString().trim()
    if (text.isEmpty()) {
        onEmpty()
        return true
    }
    return false
}


fun TextInputEditText.setUpPasswordVisibility(isPasswordVisible: Boolean, til: TextInputLayout, context: Context): Boolean {
    if (isPasswordVisible) {
        this.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        til.endIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_close_eye)
    } else {
        this.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        til.endIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_open_eye)
    }

    this.setSelection(this.text?.length ?: 0)
    return !isPasswordVisible
}

