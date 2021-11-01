package hu.bme.aut.android.sharedshoppinglist.util

import android.content.DialogInterface
import android.view.View
import androidx.appcompat.app.AlertDialog

fun AlertDialog.setPositiveButtonWithValidation(method: () -> Unit) {
    getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(DialogValidationListener { method() })
}

private class DialogValidationListener(private val method: () -> Unit) : View.OnClickListener {
    override fun onClick(v: View?) {
        method()
    }
}