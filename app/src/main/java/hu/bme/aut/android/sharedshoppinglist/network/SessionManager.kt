package hu.bme.aut.android.sharedshoppinglist.network

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)

    companion object {
        const val SHARED_PREF = "S_SHOPPING_LIST"
        const val USER_JWT_TOKEN = "user_token"
        const val USER_REFRESH_TOKEN = "user_refresh_token"
        const val USER_LOGGED_IN = "user_logged_in"
    }

    fun saveAuthToken(token: String) {
        with(prefs.edit()) {
            putString(USER_JWT_TOKEN, token)
            apply()
        }
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(USER_JWT_TOKEN, null)
    }

    fun saveRefreshToken(refreshToken: String) {
        with(prefs.edit()) {
            putString(USER_REFRESH_TOKEN, refreshToken)
            apply()
        }
    }

    fun fetchRefreshToken(): String? {
        return prefs.getString(USER_REFRESH_TOKEN, null)
    }

    fun loginUser(tokenModel: TokenModel) {
        with(prefs.edit()) {
            putBoolean(USER_LOGGED_IN, true)
            putString(USER_JWT_TOKEN, tokenModel.token)
            putString(USER_REFRESH_TOKEN, tokenModel.refreshToken)
            apply()
        }
    }

    fun logoutUser() {
        with(prefs.edit()) {
            putBoolean(USER_LOGGED_IN, false)
            putString(USER_JWT_TOKEN, null)
            putString(USER_REFRESH_TOKEN, null)
            apply()
        }
    }

    fun getUserLoggedIn(): Boolean {
        return prefs.getBoolean(USER_LOGGED_IN, false)
    }
}