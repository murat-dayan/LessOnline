package com.muratdayan.lessonline.presentation.util

import android.content.Context

class PreferenceHelper(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("lesson_line_prefs",Context.MODE_PRIVATE)

    fun saveUserLoginStatus(isLoggedIn:Boolean){
        sharedPreferences.edit().putBoolean("is_logged_in",isLoggedIn).apply()
    }

    fun isUserLoggedIn():Boolean{
        return sharedPreferences.getBoolean("is_logged_in",false)
    }


}