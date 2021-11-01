package hu.bme.aut.android.sharedshoppinglist.model

import java.time.LocalDateTime

data class Product(
    val ID: Long,
    val ShoppingListID: Long,
    val Name: String,
    val isShared: Boolean,
    val PlannedPrice: Long,
    val ActualPrice: Long?,
    val AddedAt: LocalDateTime,
    val BoughtAt: LocalDateTime?,
    val AddedByID: Long,
    var BoughtByID: Long?
)