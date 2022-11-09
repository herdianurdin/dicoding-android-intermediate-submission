package com.saeware.storyapp.utils

import android.content.Context
import android.widget.Toast

object MessageUtility {
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}