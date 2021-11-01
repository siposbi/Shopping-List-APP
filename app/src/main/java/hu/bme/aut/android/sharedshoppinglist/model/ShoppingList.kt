package hu.bme.aut.android.sharedshoppinglist.model

import java.time.LocalDateTime

data class ShoppingList(
    val ID: Long,
    val isShared: Boolean,
    var Name: String,
    val ShareCode: String,
    val CreatedAt: LocalDateTime,
    val LastEditedAt: LocalDateTime
)