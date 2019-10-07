package com.gabrielpozo.openapp.util.extensions

import android.content.Context
import androidx.annotation.StringRes
import com.afollestad.materialdialogs.MaterialDialog
import com.gabrielpozo.openapi.R

fun Context.displayToast(@StringRes message: Int) {

}

fun Context.displayToast(message: String) {

}

fun Context.displaySuccessDialog(message: String) {
    MaterialDialog(this).show {
        title(R.string.text_success)
            .message(text = message)
            .positiveButton(R.string.text_ok)
    }

}

fun Context.displayErrorDialog(message: String) {
    MaterialDialog(this).show {
        title(R.string.text_error)
            .message(text = message)
            .positiveButton(R.string.text_ok)
    }
}