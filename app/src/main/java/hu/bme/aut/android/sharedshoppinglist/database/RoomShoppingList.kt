package hu.bme.aut.android.sharedshoppinglist.database

import androidx.room.Entity

@Entity(tableName = "shopping_list")
data class RoomShoppingList(
    val id: Long,
    val numberOfProducts: Long,
)