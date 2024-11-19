package com.muratdayan.core.util

import android.view.View
import androidx.navigation.Navigation


fun Navigation.goBack(view: View) {
    val navController = findNavController(view)
    navController.popBackStack()
}