package hu.bme.aut.android.sharedshoppinglist.util

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Fragment.showSnackBar(
    title: String,
    length: Int = Snackbar.LENGTH_SHORT,
    actionText: String? = null,
    action: (() -> Unit)? = null,
    anchor: View? = null
) {
    val snackBar = Snackbar.make(view!!, title, length)
    if (anchor != null) snackBar.anchorView = anchor
    if (actionText != null && action != null) snackBar.setAction(actionText) { action() }
    snackBar.show()
}

fun Fragment.showSnackBar(
    title: Int,
    length: Int = Snackbar.LENGTH_SHORT,
    actionText: Int? = null,
    action: (() -> Unit)? = null,
    anchor: View? = null
) {
    showSnackBar(
        title = context!!.getString(title),
        length = length,
        actionText = actionText?.let { context!!.getString(it) },
        action = action,
        anchor = anchor
    )
}