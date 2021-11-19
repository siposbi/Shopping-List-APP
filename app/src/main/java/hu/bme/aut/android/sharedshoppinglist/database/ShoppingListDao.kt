package hu.bme.aut.android.sharedshoppinglist.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ShoppingListDao {

    @Insert
    suspend fun insertShoppingList(shoppingList: RoomShoppingList)

    @Query("SELECT * FROM shopping_list")
    suspend fun getAllShoppingLists(): List<RoomShoppingList>

    @Update
    suspend fun updateShoppingList(shoppingList: RoomShoppingList)

    @Query("UPDATE shopping_list SET numberOfProducts = :numberOfProducts WHERE id = :id")
    suspend fun updateShoppingList(id: Long, numberOfProducts: Long)

    @Query("UPDATE shopping_list SET numberOfProducts = (SELECT numberOfProducts FROM shopping_list) - 1 WHERE id = :shoppingListId")
    suspend fun deleteProduct(shoppingListId: Long)

    @Query("UPDATE shopping_list SET numberOfProducts = (SELECT numberOfProducts FROM shopping_list) + 1 WHERE id = :shoppingListId")
    suspend fun addProduct(shoppingListId: Long)

    @Query("DELETE FROM shopping_list WHERE id = :shoppingListId")
    suspend fun deleteShoppingListById(shoppingListId: Long)
}