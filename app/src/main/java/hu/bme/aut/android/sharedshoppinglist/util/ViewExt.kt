package hu.bme.aut.android.sharedshoppinglist.util

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.showSnackBar(
    title: Int,
    length: Int = Snackbar.LENGTH_SHORT,
    actionText: Int? = null,
    action: (() -> Unit)? = null,
    anchor: View? = null
) {
    val snackBar = Snackbar.make(this, title, length)
    if (anchor != null) snackBar.anchorView = anchor
    if (actionText != null && action != null) snackBar.setAction(actionText) { action() }
    snackBar.show()
}