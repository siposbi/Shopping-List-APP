package hu.bme.aut.android.sharedshoppinglist.model

import java.time.LocalDateTime

data class Member(
    val userId: Long,
    val firstName: String,
    val lastName: String,
    val joinDateTime: LocalDateTime,
    val isOwner: Boolean
)