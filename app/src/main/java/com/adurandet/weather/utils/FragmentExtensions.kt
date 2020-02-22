package com.adurandet.weather.utils

import androidx.fragment.app.Fragment
import com.adurandet.weather.R
import com.google.android.material.snackbar.Snackbar

fun Fragment.showError(message: String?) {
    val errorMessage = if (message.isNullOrEmpty()) {
        getString(R.string.generic_error_message)
    } else {
        message
    }
    activity?.let{
        Snackbar.make(it.findViewById(R.id.coordinatorLayout), errorMessage, Snackbar.LENGTH_LONG).show()
    }
}