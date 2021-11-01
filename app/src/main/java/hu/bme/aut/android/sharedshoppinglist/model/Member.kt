package hu.bme.aut.android.sharedshoppinglist.model

import java.time.LocalDateTime

data class Member(
    val ID: Long,
    val firstName: String,
    val lastName: String,
    val isOwner: Boolean,
    val joinDate: LocalDateTime
)