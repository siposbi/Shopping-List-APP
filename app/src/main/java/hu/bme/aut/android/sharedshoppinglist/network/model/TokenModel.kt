package hu.bme.aut.android.sharedshoppinglist.network.model

import java.time.LocalDateTime

data class TokenModel(
    val token: String,
    val refreshToken: String,
    val tokenValidUntil: LocalDateTime,
    val refreshTokenValidUntil: LocalDateTime
)

data class RefreshTokenModel(
    val token: String,
    val refreshToken: String
)