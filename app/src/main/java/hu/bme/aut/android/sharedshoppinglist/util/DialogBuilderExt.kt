package hu.bme.aut.android.sharedshoppinglist.util

import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun MaterialAlertDialogBuilder.setDismissButton(textId: Int): MaterialAlertDialogBuilder {
    setNegativeButton(textId) { dialog, _ -> dialog.dismiss() }
    return this
}

fun MaterialAlertDialogBuilder.setPositiveButtonText(textId: Int): MaterialAlertDialogBuilder {
    setPositiveButton(textId, null)
    return this
}

fun AlertDialog.setPositiveButtonOnShow(method: (AlertDialog) -> Unit): AlertDialog {
    setOnShowListener {
        val positiveButton = getButton(DialogInterface.BUTTON_POSITIVE)
        positiveButton.setOnClickListener { method(this) }
    }
    return this
}