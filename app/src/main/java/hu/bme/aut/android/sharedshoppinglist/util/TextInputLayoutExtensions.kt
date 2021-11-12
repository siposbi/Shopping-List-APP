package hu.bme.aut.android.sharedshoppinglist.util

import android.content.Context
import com.google.android.material.textfield.TextInputLayout
import hu.bme.aut.android.sharedshoppinglist.R


fun TextInputLayout.clearErrorIfRequiredValid(context: Context) {
    if (this.error != null && checkAndShowIfRequiredFilled(context)) {
        this.error = null
    }
}

fun TextInputLayout.checkAndShowIfRequiredFilled(context: Context): Boolean {
    if (this.editText!!.text.toString().isNotEmpty()) {
        return true
    }
    this.error = context.getString(R.string.required_error)
    return false
}

fun TextInputLayout.clearErrorIfLengthValid(context: Context, maximum: Int) {
    if (this.error != null && checkAndShowIfLengthValid(context, maximum)) {
        this.error = null
    }
}

fun TextInputLayout.checkAndShowIfLengthValid(context: Context, maximum: Int): Boolean {
    if (this.editText!!.text.toString().length <= maximum) {
        return true
    }
    this.error = context.getString(R.string.max_length)
    return false
}