package hu.bme.aut.android.sharedshoppinglist.network

import hu.bme.aut.android.sharedshoppinglist.model.ShoppingList
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ShoppingListAPI {
    companion object{
        const val BASE_URL = "https://shoppinglistapipz1pqy.azurewebsites.net/api/"
        const val AUTH = "Auth"
        const val PRODUCTS = "Product"
        const val SHOPPING_LIST = "ShoppingList"
    }


    @POST("$AUTH/login")
    fun login(@Body request: LoginModel): Call<ResponseModel<TokenModel>>

    @POST("$AUTH/register")
    fun register(@Body registerModel: RegisterModel): Call<ResponseModel<Long>>

    @GET("$SHOPPING_LIST/getAllForUser")
    fun getShoppingLists(): Call<ResponseModel<List<ShoppingList>>>
}