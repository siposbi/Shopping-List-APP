package hu.bme.aut.android.sharedshoppinglist.model

import java.time.LocalDateTime

data class ShoppingList(
    val id: Long,
    val name: String,
    val numberOfProducts: Long,
    val shareCode: String,
    val createdDateTime: LocalDateTime,
    val lastEditedDateTime: LocalDateTime,
    val isShared: Boolean
)