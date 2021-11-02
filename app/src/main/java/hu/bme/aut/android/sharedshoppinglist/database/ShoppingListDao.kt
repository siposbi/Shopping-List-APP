package hu.bme.aut.android.sharedshoppinglist.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ShoppingListDao {

    @Insert
    fun insertShoppingList(shoppingList: RoomShoppingList)

    @Query("SELECT * FROM shopping_list")
    fun getAllShoppingLists(): List<RoomShoppingList>

    @Update
    fun updateShoppingList(shoppingList: RoomShoppingList): Long

    @Query("UPDATE shopping_list SET numberOfProducts = :numberOfProducts WHERE id = :id")
    fun updateShoppingList(id: Long, numberOfProducts: Long)

    @Query("DELETE FROM shopping_list WHERE id = :shoppingListId")
    fun deleteShoppingListById(shoppingListId: Long)
}