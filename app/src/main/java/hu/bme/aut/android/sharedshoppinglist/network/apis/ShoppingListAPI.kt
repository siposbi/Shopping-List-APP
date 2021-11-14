package hu.bme.aut.android.sharedshoppinglist.network.apis

import hu.bme.aut.android.sharedshoppinglist.model.Export
import hu.bme.aut.android.sharedshoppinglist.model.Member
import hu.bme.aut.android.sharedshoppinglist.model.ShoppingList
import hu.bme.aut.android.sharedshoppinglist.network.LoginModel
import hu.bme.aut.android.sharedshoppinglist.network.RegisterModel
import hu.bme.aut.android.sharedshoppinglist.network.ResponseModel
import hu.bme.aut.android.sharedshoppinglist.network.TokenModel
import retrofit2.Call
import retrofit2.http.*

interface ShoppingListAPI {
    companion object {
        const val SHOPPING_LIST = "ShoppingList"
    }

    @GET("$SHOPPING_LIST/getAllForUser")
    fun getShoppingLists(): Call<ResponseModel<List<ShoppingList>>>

    @POST("${SHOPPING_LIST}/create")
    fun create(@Body name: String): Call<ResponseModel<ShoppingList>>

    @PUT("${SHOPPING_LIST}/join/{shareCode}")
    fun join(@Path("shareCode") shareCode: String): Call<ResponseModel<ShoppingList>>

    @PUT("${SHOPPING_LIST}/leave/{listId}")
    fun leave(@Path("listId") listId: Long): Call<ResponseModel<Long>>

    @PUT("${SHOPPING_LIST}/rename/{listId}")
    fun rename(@Path("listId") listId: Long, @Body newName: String): Call<ResponseModel<ShoppingList>>

    @GET("${SHOPPING_LIST}/getMembers/{listId}")
    fun getMembers(@Path("listId") listId: Long): Call<ResponseModel<List<Member>>>

    @GET("${SHOPPING_LIST}/getExport/{listId}")
    fun getExport(@Path("listId") listId: Long): Call<ResponseModel<List<Export>>>
}