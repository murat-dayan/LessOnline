package com.muratdayan.lessonline.presentation.util

import android.widget.EditText

fun EditText.doIfIsEmptyAndReturn(onEmpty: () -> Unit): Boolean {
    val text = this.text.toString().trim()
    if (text.isEmpty()) {
        onEmpty()
        return true
    }
    return false
}