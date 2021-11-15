package hu.bme.aut.android.sharedshoppinglist.network

import android.content.Context
import android.content.SharedPreferences
import hu.bme.aut.android.sharedshoppinglist.network.model.RefreshTokenModel
import hu.bme.aut.android.sharedshoppinglist.network.model.TokenModel
import java.time.LocalDateTime
import java.time.ZoneOffset

class SessionManager(context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
    private var dateTimeFormatter = LocalDateTimeAdapter()

    companion object {
        const val SHARED_PREF = "S_SHOPPING_LIST"
        const val USER_JWT_TOKEN = "user_token"
        const val USER_REFRESH_TOKEN = "user_refresh_token"
        const val USER_LOGGED_IN = "user_logged_in"
        const val TOKEN_VALID_UNTIL = "token_valid_until"
        const val REFRESH_TOKEN_VALID_UNTIL = "refresh_token_valid_until"
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(USER_JWT_TOKEN, null)
    }

    fun loginUser(tokenModel: TokenModel) {
        with(prefs.edit()) {
            putBoolean(USER_LOGGED_IN, true)
            putString(USER_JWT_TOKEN, tokenModel.token)
            putString(USER_REFRESH_TOKEN, tokenModel.refreshToken)
            putString(TOKEN_VALID_UNTIL, dateTimeFormatter.toJson(tokenModel.tokenValidUntil))
            putString(
                REFRESH_TOKEN_VALID_UNTIL,
                dateTimeFormatter.toJson(tokenModel.refreshTokenValidUntil)
            )
            apply()
        }
    }

    fun logoutUser() {
        with(prefs.edit()) {
            putBoolean(USER_LOGGED_IN, false)
            putString(USER_JWT_TOKEN, null)
            putString(USER_REFRESH_TOKEN, null)
            putString(TOKEN_VALID_UNTIL, null)
            putString(REFRESH_TOKEN_VALID_UNTIL, null)
            apply()
        }
    }

    fun getUserLoggedIn(): Boolean {
        return prefs.getBoolean(USER_LOGGED_IN, false)
    }

    fun getAuthTokenValid(): Boolean {
        val validUntilString = prefs.getString(TOKEN_VALID_UNTIL, null) ?: return false
        return dateTimeFormatter.fromJson(validUntilString) >= LocalDateTime.now(ZoneOffset.UTC)
    }

    fun getRefreshAuthTokenValid(): Boolean {
        val validUntilString = prefs.getString(REFRESH_TOKEN_VALID_UNTIL, null) ?: return false
        return dateTimeFormatter.fromJson(validUntilString) >= LocalDateTime.now(ZoneOffset.UTC)
    }

    fun getRefreshTokenModel(): RefreshTokenModel {
        return RefreshTokenModel(
            token = prefs.getString(USER_JWT_TOKEN, "")!!,
            refreshToken = prefs.getString(USER_REFRESH_TOKEN, "")!!
        )
    }
}