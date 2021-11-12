package hu.bme.aut.android.sharedshoppinglist.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_list")
data class RoomShoppingList(
    @PrimaryKey
    val id: Long,
    val numberOfProducts: Long,
)