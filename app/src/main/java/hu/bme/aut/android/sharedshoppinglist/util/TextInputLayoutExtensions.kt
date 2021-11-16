package hu.bme.aut.android.sharedshoppinglist.util

import android.content.Context
import android.icu.text.DecimalFormatSymbols
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputLayout
import hu.bme.aut.android.sharedshoppinglist.R


fun TextInputLayout.requiredValid(context: Context): Boolean {
    editText?.doAfterTextChanged {
        if (checkAndShowIfRequiredFilled(context))
            error = null
    }
    return checkAndShowIfRequiredFilled(context)
}

fun TextInputLayout.lengthValid(context: Context, maximum: Int): Boolean {
    editText?.doAfterTextChanged {
        if (checkAndShowIfLengthValid(context, maximum))
            error = null
    }
    return checkAndShowIfLengthValid(context, maximum)
}

fun TextInputLayout.requiredAndLengthValid(context: Context, maximum: Int): Boolean {
    editText?.doAfterTextChanged {
        val requiredValid = checkAndShowIfRequiredFilled(context)
        val lengthValid = checkAndShowIfLengthValid(context, maximum)
        if (requiredValid && lengthValid) {
            error = null
        }
    }
    return checkAndShowIfRequiredFilled(context) && checkAndShowIfLengthValid(context, maximum)
}

private fun TextInputLayout.checkAndShowIfLengthValid(context: Context, maximum: Int): Boolean {
    if (this.text.length <= maximum) {
        return true
    }
    this.error = context.getString(R.string.max_length)
    return false
}

private fun TextInputLayout.checkAndShowIfRequiredFilled(context: Context): Boolean {
    if (this.text.isNotEmpty()) {
        return true
    }
    this.error = context.getString(R.string.required_error)
    return false
}

var TextInputLayout.text: String
    get() = editText?.text?.toString() ?: ""
    set(value) {
        editText?.setText(value)
    }

fun TextInputLayout.getPriceAsLong(): Long {
    val separatorPosition = text.indexOf(DecimalFormatSymbols.getInstance().decimalSeparator)
    if (separatorPosition == -1)
        return text.toLong() * 100

    val dollars = text.substring(0, separatorPosition)
    val cents = text.substring(separatorPosition + 1, separatorPosition + 3)
    return ("$dollars$cents").toLong()
}