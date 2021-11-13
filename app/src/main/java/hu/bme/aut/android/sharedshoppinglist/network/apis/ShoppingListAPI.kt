package hu.bme.aut.android.sharedshoppinglist.network.apis

import hu.bme.aut.android.sharedshoppinglist.model.ShoppingList
import hu.bme.aut.android.sharedshoppinglist.network.LoginModel
import hu.bme.aut.android.sharedshoppinglist.network.RegisterModel
import hu.bme.aut.android.sharedshoppinglist.network.ResponseModel
import hu.bme.aut.android.sharedshoppinglist.network.TokenModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ShoppingListAPI {
    companion object{
        const val SHOPPING_LIST = "ShoppingList"
    }

    @GET("$SHOPPING_LIST/getAllForUser")
    fun getShoppingLists(): Call<ResponseModel<List<ShoppingList>>>
}