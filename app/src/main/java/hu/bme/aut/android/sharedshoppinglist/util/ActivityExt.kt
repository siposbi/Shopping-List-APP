package hu.bme.aut.android.sharedshoppinglist.util

import android.app.Activity
import android.content.Context

const val USER_LOGGED_IN_KEY = "IS_USER_LOGGED_IN"

fun Activity.setUserLoggedIn(boolean: Boolean) {
    val sharedPref = getPreferences(Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putBoolean(USER_LOGGED_IN_KEY, boolean)
        apply()
    }
}
